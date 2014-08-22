package com.sicpa.standard.sasscl.controller.productionconfig;

import com.sicpa.standard.sasscl.controller.productionconfig.config.*;

import java.util.Collection;

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

    BrsConfig getBrsConfig();

	String getAuthenticatorMode();

	String getActivationBehavior();
}
