package com.sicpa.standard.sasscl.controller.productionconfig.factory;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.utils.Cache;
import com.sicpa.standard.sasscl.controller.productionconfig.ConfigurationAction;
import com.sicpa.standard.sasscl.controller.productionconfig.ICameraFactoryMapping;
import com.sicpa.standard.sasscl.controller.productionconfig.IConfigurable;
import com.sicpa.standard.sasscl.controller.productionconfig.IConfigurator;
import com.sicpa.standard.sasscl.controller.productionconfig.IDeviceFactory;
import com.sicpa.standard.sasscl.controller.productionconfig.IDeviceModelNamePostfixProperty;
import com.sicpa.standard.sasscl.controller.productionconfig.IImplementationProvider;
import com.sicpa.standard.sasscl.controller.productionconfig.IPrinterFactoryMapping;
import com.sicpa.standard.sasscl.controller.productionconfig.config.AbstractLayoutConfig;
import com.sicpa.standard.sasscl.controller.productionconfig.config.BisConfig;
import com.sicpa.standard.sasscl.controller.productionconfig.config.CameraConfig;
import com.sicpa.standard.sasscl.controller.productionconfig.config.PlcConfig;
import com.sicpa.standard.sasscl.controller.productionconfig.config.PrinterConfig;
import com.sicpa.standard.sasscl.devices.IStartableDevice;
import com.sicpa.standard.sasscl.devices.plc.IPlcAdaptor;

public class DeviceFactory implements IDeviceFactory {

	private static final Logger logger = LoggerFactory.getLogger(DeviceFactory.class);

	protected IImplementationProvider implementationProvider;
	protected Cache cache = new Cache();

	protected ICameraFactoryMapping cameraFactoryMapping;
	protected IPrinterFactoryMapping printerFactoryMapping;

	protected IDeviceModelNamePostfixProperty deviceModelNamePostfixProperty;

	protected final List<ConfigurationAction> configurationAction = new ArrayList<ConfigurationAction>();

	@Override
	public IStartableDevice getCamera(CameraConfig cameraConfig) {
		IStartableDevice value = cache.get(cameraConfig.getId(), IStartableDevice.class);
		if (value == null) {
			deviceModelNamePostfixProperty.set(cameraConfig.getId());
			String beanName = cameraFactoryMapping.getCameraBeanName(cameraConfig.getCameraType());
			logger.info("retreiving camera from implProvider {}", beanName);
			value = (IStartableDevice) implementationProvider.getImplementation(beanName);
			value.setName(cameraConfig.getId());
			cache.put(cameraConfig.getId(), IStartableDevice.class, value);
			createConfigurator(value, cameraConfig);
		}
		return value;
	}

	@Override
	public IStartableDevice getPrinter(PrinterConfig printerConfig) {
		IStartableDevice value = cache.get(printerConfig.getId(), IStartableDevice.class);
		if (value == null) {
			deviceModelNamePostfixProperty.set(printerConfig.getId());
			String beanName = printerFactoryMapping.getPrinterBeanName(printerConfig.getPrinterType());
			logger.info("retreiving printer from implProvider {} - {}", beanName, printerConfig.getId());
			value = (IStartableDevice) implementationProvider.getImplementation(beanName);
			value.setName(printerConfig.getId());
			cache.put(printerConfig.getId(), IStartableDevice.class, value);
			createConfigurator(value, printerConfig);
		}
		return value;
	}

	@Override
	public IStartableDevice getBis(BisConfig bisConfig) {
		IStartableDevice value = cache.get(bisConfig.getId(), IStartableDevice.class);
		if (value == null) {
			deviceModelNamePostfixProperty.set(bisConfig.getId());
			String beanName = "bis";
			logger.info("retreiving printer from implProvider {} - {}", beanName, bisConfig.getId());
			value = (IStartableDevice) implementationProvider.getImplementation(beanName);
			cache.put(bisConfig.getId(), IStartableDevice.class, value);
			createConfigurator(value, bisConfig);
		}
		return value;
	}

	@Override
	public IPlcAdaptor getPlc(PlcConfig config) {
		IPlcAdaptor value = cache.get("plc", IPlcAdaptor.class);
		if (value == null) {
			value = (IPlcAdaptor) implementationProvider.getImplementation("plc");
			cache.put("plc", IPlcAdaptor.class, value);
			createConfigurator(value, config);
		}
		return value;
	}

	@SuppressWarnings("unchecked")
	protected void createConfigurator(Object configurable, AbstractLayoutConfig config) {
		if (configurable instanceof IConfigurable) {
			IConfigurator<AbstractLayoutConfig, Object> configurator = ((IConfigurable<AbstractLayoutConfig, Object>) configurable)
					.getConfigurator();
			configurationAction.add(new ConfigurationAction(configurator, config, configurable));
		} else {
			logger.warn(configurable + " is not configurable");
		}
	}

	/**
	 * this has to be called after the creation of all camera/printer/qc as the configurationAction are created in the
	 * getXXX(XXXconfig)
	 */
	@Override
	public List<ConfigurationAction> getConfigurationActions() {
		return configurationAction;
	}

	public void setImplementationProvider(IImplementationProvider implementationProvider) {
		this.implementationProvider = implementationProvider;
	}

	public void setCameraFactoryMapping(ICameraFactoryMapping cameraFactoryMapping) {
		this.cameraFactoryMapping = cameraFactoryMapping;
	}

	public void setPrinterFactoryMapping(IPrinterFactoryMapping printerFactoryMapping) {
		this.printerFactoryMapping = printerFactoryMapping;
	}

	public void setDeviceModelNamePostfixProperty(IDeviceModelNamePostfixProperty deviceModelNamePostfixProperty) {
		this.deviceModelNamePostfixProperty = deviceModelNamePostfixProperty;
	}

	@Override
	public void reset() {
		configurationAction.clear();
		cache.clear();
	}

}
