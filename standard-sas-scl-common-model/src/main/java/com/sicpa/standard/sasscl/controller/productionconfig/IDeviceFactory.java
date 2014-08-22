package com.sicpa.standard.sasscl.controller.productionconfig;

import com.sicpa.standard.sasscl.controller.productionconfig.config.*;
import com.sicpa.standard.sasscl.devices.IStartableDevice;
import com.sicpa.standard.sasscl.devices.plc.IPlcAdaptor;

import java.util.List;

public interface IDeviceFactory {

	/**
	 * 
	 * @return the camera that correspond to the cameraConfig, called during the connecting phase
	 */
	IStartableDevice getCamera(CameraConfig cameraConfig);

	IStartableDevice getBis(BisConfig bisConfig);

    IStartableDevice getBrs(BrsConfig bisConfig);

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
