package com.sicpa.standard.sasscl.controller.productionconfig;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.sicpa.standard.sasscl.controller.productionconfig.config.D900CameraConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.business.activation.IActivationBehavior;
import com.sicpa.standard.sasscl.controller.hardware.IHardwareController;
import com.sicpa.standard.sasscl.controller.hardware.ProductionDevicesCreatedEvent;
import com.sicpa.standard.sasscl.controller.productionconfig.config.CameraConfig;
import com.sicpa.standard.sasscl.controller.productionconfig.config.PrinterConfig;
import com.sicpa.standard.sasscl.devices.IStartableDevice;
import com.sicpa.standard.sasscl.devices.bis.IBisAdaptor;
import com.sicpa.standard.sasscl.devices.camera.ICameraAdaptor;
import com.sicpa.standard.sasscl.devices.camera.simulator.ICameraAdaptorSimulator;
import com.sicpa.standard.sasscl.devices.camera.simulator.ICodeProvider;
import com.sicpa.standard.sasscl.devices.plc.PlcLineHelper;
import com.sicpa.standard.sasscl.provider.impl.ActivationBehaviorProvider;
import com.sicpa.standard.sasscl.provider.impl.AuthenticatorModeProvider;
import com.sicpa.standard.sasscl.provider.impl.BisProvider;
import com.sicpa.standard.sasscl.provider.impl.PlcProvider;

public class ProductionInitiator implements IProductionInitiator {

	private static Logger logger = LoggerFactory.getLogger(ProductionInitiator.class);

	private IDeviceFactory deviceFactory;
	private IProductionConfig productionConfig;
	private PlcProvider plcProvider;
	private BisProvider bisProvider;
	private IStartableDevice brs;
	private IHardwareController hardwareController;
	private AuthenticatorModeProvider authenticatorModeProvider;
	private IImplementationProvider implementationProvider;
	private ActivationBehaviorProvider activationBehaviorProvider;

	private final Map<String, IStartableDevice> cameras = new HashMap<>();
	private final Map<String, IStartableDevice> d900Cameras = new HashMap<>();
	private final Map<String, IStartableDevice> printers = new HashMap<>();

	protected void reset() {
		deviceFactory.reset();
		cameras.clear();
		d900Cameras.clear();
		printers.clear();
		bisProvider.set(null);
		brs = null;
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

	private void setupActivationBehavior() {
		IActivationBehavior activationBehavior = (IActivationBehavior) implementationProvider
				.getImplementation(productionConfig.getActivationBehavior());
		activationBehaviorProvider.set(activationBehavior);
	}

	private void setAvailableLineIndexes() {
		PlcLineHelper.resetLineIndex();
		productionConfig.getPlcConfig().getLinesProperties().keySet().forEach(i -> PlcLineHelper.addLineIndex(i));
	}

	private void prepareSimulator() {
		for (IStartableDevice dev : printers.values()) {
			if (dev instanceof ICodeProvider) {
				ICameraAdaptor camera = getCameraAssociatedWithPrinter(dev.getName());
				if (camera instanceof ICameraAdaptorSimulator) {
					((ICameraAdaptorSimulator) camera).getSimulatorController().setCodeProvider(((ICodeProvider) dev));
				}
			}
		}
	}

	private ICameraAdaptor getCameraAssociatedWithPrinter(String printer) {
		if (productionConfig.getPrinterConfigs() != null) {
			for (PrinterConfig pr : productionConfig.getPrinterConfigs()) {
				if (pr.getId().equals(printer)) {
					return (ICameraAdaptor) cameras.get(pr.getValidatedBy());
				}
			}
		}
		return null;
	}

	private void createAll() {
		createD900Camera();
		createCamera();
		createPrinter();
		createPlc();
		createBis();
		createBrs();
	}

	private void createPlc() {
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

	private void injectDevicesIntoHardwareController() {
		Set<IStartableDevice> devices = new HashSet<IStartableDevice>();
		devices.addAll(cameras.values());
		devices.addAll(d900Cameras.values());
		devices.addAll(printers.values());

		if (bisProvider.get() != null) {
			devices.add(bisProvider.get());
		}

		if (brs != null) {
			devices.add(brs);
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

	private void createBis() {
		if (productionConfig.getBisConfig() != null) {
			logger.debug("creating BIS");
			bisProvider.set((IBisAdaptor) deviceFactory.getBis(productionConfig.getBisConfig()));
			logger.debug("camera created:{}", bisProvider.get().getName());
		}
	}

	private void createBrs() {
		if (productionConfig.getBrsConfig() != null) {
			logger.debug("creating BRS");
			brs = deviceFactory.getBrs(productionConfig.getBrsConfig());
			logger.debug("BRS created:{}", brs.getName());
		}
	}

	private void createCamera() {
		logger.debug("creating cameras");
		for (CameraConfig cameraConfig : productionConfig.getCameraConfigs()) {
			IStartableDevice camera = deviceFactory.getCamera(cameraConfig);
			logger.debug("camera created:{}", camera.getName());
			cameras.put(cameraConfig.getId(), camera);
		}
	}

	private void createD900Camera() {
		logger.debug("creating D900cameras");
		if(productionConfig.getD900CameraConfigs()!= null){
			for (D900CameraConfig cameraConfig : productionConfig.getD900CameraConfigs()) {
				IStartableDevice d900camera = deviceFactory.getD900Camera(cameraConfig);
				logger.debug("D900 camera created:{}", d900camera.getName());
				d900Cameras.put(cameraConfig.getId(), d900camera);
			}
		}
	}

	private void createPrinter() {
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

	public void setBisProvider(BisProvider bisProvider) {
		this.bisProvider = bisProvider;
	}
}
