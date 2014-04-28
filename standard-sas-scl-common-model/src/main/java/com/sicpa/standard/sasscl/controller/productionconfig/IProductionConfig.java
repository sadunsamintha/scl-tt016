package com.sicpa.standard.sasscl.controller.productionconfig;

import java.util.Collection;

import com.sicpa.standard.sasscl.controller.productionconfig.config.BisConfig;
import com.sicpa.standard.sasscl.controller.productionconfig.config.CameraConfig;
import com.sicpa.standard.sasscl.controller.productionconfig.config.PlcConfig;
import com.sicpa.standard.sasscl.controller.productionconfig.config.PrinterConfig;

/**
 * represent the config to use for a production
 * 
 * @author DIelsch
 * 
 */
public interface IProductionConfig {

	/**
	 * 
	 * @return the config of the cameras that will be created for the production
	 */
	Collection<CameraConfig> getCameraConfigs();

	/**
	 * 
	 * @return the config of the printers that will be created for the production
	 */
	Collection<PrinterConfig> getPrinterConfigs();

	PlcConfig getPlcConfig();

	BisConfig getBisConfig();

	String getAuthenticatorMode();

	String getActivationBehavior();

}
