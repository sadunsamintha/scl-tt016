package com.sicpa.standard.sasscl.controller.productionconfig;

import java.util.List;

import com.sicpa.standard.sasscl.controller.productionconfig.config.BisConfig;
import com.sicpa.standard.sasscl.controller.productionconfig.config.CameraConfig;
import com.sicpa.standard.sasscl.controller.productionconfig.config.PlcConfig;
import com.sicpa.standard.sasscl.controller.productionconfig.config.PrinterConfig;
import com.sicpa.standard.sasscl.devices.IStartableDevice;
import com.sicpa.standard.sasscl.devices.plc.IPlcAdaptor;

public interface IDeviceFactory {

	/**
	 * 
	 * @return the camera that correspond to the cameraConfig, called during the connecting phase
	 */
	IStartableDevice getCamera(CameraConfig cameraConfig);

	IStartableDevice getBis(BisConfig bisConfig);

	/**
	 * 
	 * @return the printer that correspond to the printerConfig, called during the connecting phase
	 */
	IStartableDevice getPrinter(PrinterConfig printerConfig);

	IPlcAdaptor getPlc(PlcConfig plcConfig);

	/**
	 * 
	 * @return the configurators for the printers/camera/qc/codesProviders/etc...
	 */
	List<ConfigurationAction> getConfigurationActions();

	void reset();

}
