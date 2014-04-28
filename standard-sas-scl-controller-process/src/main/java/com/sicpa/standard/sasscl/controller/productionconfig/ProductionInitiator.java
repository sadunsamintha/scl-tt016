package com.sicpa.standard.sasscl.controller.productionconfig;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.business.activation.IActivationBehavior;
import com.sicpa.standard.sasscl.controller.hardware.IHardwareController;
import com.sicpa.standard.sasscl.controller.hardware.ProductionDevicesCreatedEvent;
import com.sicpa.standard.sasscl.controller.productionconfig.config.CameraConfig;
import com.sicpa.standard.sasscl.controller.productionconfig.config.PrinterConfig;
import com.sicpa.standard.sasscl.devices.IStartableDevice;
import com.sicpa.standard.sasscl.devices.camera.ICameraAdaptor;
import com.sicpa.standard.sasscl.devices.camera.simulator.ICameraAdaptorSimulator;
import com.sicpa.standard.sasscl.devices.plc.PlcVariableMap;
import com.sicpa.standard.sasscl.devices.printer.simulator.IPrinterAdaptorSimulator;
import com.sicpa.standard.sasscl.provider.impl.ActivationBehaviorProvider;
import com.sicpa.standard.sasscl.provider.impl.AuthenticatorModeProvider;
import com.sicpa.standard.sasscl.provider.impl.PlcProvider;

public class ProductionInitiator implements IProductionInitiator {

	private static Logger logger = LoggerFactory.getLogger(ProductionInitiator.class);

	protected final Map<String, IStartableDevice> cameras = new HashMap<String, IStartableDevice>();
	protected final Map<String, IStartableDevice> printers = new HashMap<String, IStartableDevice>();

	protected IDeviceFactory deviceFactory;

	protected IProductionConfig productionConfig;

	protected PlcProvider plcProvider;
	protected IStartableDevice bis;

	protected IHardwareController hardwareController;

	protected AuthenticatorModeProvider authenticatorModeProvider;

	protected IImplementationProvider implementationProvider;

	protected ActivationBehaviorProvider activationBehaviorProvider;

	protected void reset() {
		deviceFactory.reset();
		cameras.clear();
		printers.clear();
	}

	@Override
	public void init(IProductionConfig productionConfig) {
		this.productionConfig = productionConfig;
		reset();
		authenticatorModeProvider.set(productionConfig.getAuthenticatorMode());
		setupActivationBehavior();
		createAll();
		configureAll();
		injectDevicesIntoHardwareController();
		prepareSimulator();
	}

	protected void setupActivationBehavior() {
		IActivationBehavior activationBehavior = (IActivationBehavior) implementationProvider
				.getImplementation(productionConfig.getActivationBehavior());
		activationBehaviorProvider.set(activationBehavior);
	}

	protected void setAvailableLineIndexes() {
		int index1 = Integer.parseInt(productionConfig.getPlcConfig().getLine1Index());
		PlcVariableMap.addLineIndex(index1);

		String sIndex2 = productionConfig.getPlcConfig().getLine2Index();
		if (StringUtils.isNotEmpty(sIndex2)) {
			int index2 = Integer.parseInt(sIndex2);
			PlcVariableMap.addLineIndex(index2);
		}
	}

	protected void prepareSimulator() {
		for (IStartableDevice dev : printers.values()) {
			if (dev instanceof IPrinterAdaptorSimulator) {
				ICameraAdaptor camera = getCameraAssociatedWithPrinter(dev.getName());
				if (camera instanceof ICameraAdaptorSimulator) {
					((ICameraAdaptorSimulator) camera).getSimulatorController().setCodeProvider(
							((IPrinterAdaptorSimulator) dev).getSimulatorController());
				}
			}
		}
	}

	protected ICameraAdaptor getCameraAssociatedWithPrinter(String printer) {
		if (productionConfig.getPrinterConfigs() != null) {
			for (PrinterConfig pr : productionConfig.getPrinterConfigs()) {
				if (pr.getId().equals(printer)) {
					return (ICameraAdaptor) cameras.get(pr.getValidatedBy());
				}
			}
		}
		return null;
	}

	protected void createAll() {
		createCamera();
		createPrinter();
		createPlc();
		createBis();
	}

	protected void createPlc() {
		logger.debug("creating plc");
		setAvailableLineIndexes();
		plcProvider.set(deviceFactory.getPlc(productionConfig.getPlcConfig()));
	}

	public void configureAll() {
		logger.debug("executing configuration");
		for (ConfigurationAction action : deviceFactory.getConfigurationActions()) {
			try {
				action.execute();
			} catch (ConfigurationFailedException e) {
				logger.error("", e);
			}
		}
	}

	protected void injectDevicesIntoHardwareController() {
		Set<IStartableDevice> devices = new HashSet<IStartableDevice>();
		devices.addAll(cameras.values());
		devices.addAll(printers.values());
		if (bis != null) {
			devices.add(bis);
		}
		String devicesName = "";
		for (IStartableDevice dev : devices) {
			devicesName += dev.getName() + " ";
		}
		logger.debug("injecting devices into hardwarecontroller " + devicesName);
		hardwareController.setDevices(devices);
		ProductionDevicesCreatedEvent evt = new ProductionDevicesCreatedEvent(devices);
		EventBusService.post(evt);
	}

	protected void createBis() {
		if (productionConfig.getBisConfig() != null) {
			logger.debug("creating BIS");
			bis = deviceFactory.getBis(productionConfig.getBisConfig());
			logger.debug("camera created:{}", bis.getName());
		}
	}

	protected void createCamera() {
		logger.debug("creating cameras");
		for (CameraConfig cameraConfig : productionConfig.getCameraConfigs()) {
			IStartableDevice camera = deviceFactory.getCamera(cameraConfig);
			logger.debug("camera created:{}", camera.getName());
			cameras.put(cameraConfig.getId(), camera);
		}
	}

	protected void createPrinter() {
		logger.debug("creating printers");
		if (productionConfig.getPrinterConfigs() != null) {
			for (PrinterConfig printerConfig : productionConfig.getPrinterConfigs()) {
				IStartableDevice printer = deviceFactory.getPrinter(printerConfig);
				logger.debug("printer created:{}", printer.getName());
				printers.put(printerConfig.getId(), printer);
			}
		}
	}

	public void setDeviceFactory(IDeviceFactory deviceFactory) {
		this.deviceFactory = deviceFactory;
	}

	// @Override
	// public void prepare() {
	// hardwareController.close();
	// }
	public void setHardwareController(IHardwareController hardwareController) {
		this.hardwareController = hardwareController;
	}

	public void setPlcProvider(PlcProvider plcProvider) {
		this.plcProvider = plcProvider;
	}

	public void setAuthenticatorModeProvider(AuthenticatorModeProvider authenticatorModeProvider) {
		this.authenticatorModeProvider = authenticatorModeProvider;
	}

	public void setImplementationProvider(IImplementationProvider implementationProvider) {
		this.implementationProvider = implementationProvider;
	}

	public void setActivationBehaviorProvider(ActivationBehaviorProvider activationBehaviorProvider) {
		this.activationBehaviorProvider = activationBehaviorProvider;
	}
}
