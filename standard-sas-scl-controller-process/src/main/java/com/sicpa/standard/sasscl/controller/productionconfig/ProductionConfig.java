package com.sicpa.standard.sasscl.controller.productionconfig;

import java.util.ArrayList;
import java.util.List;

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
public class ProductionConfig implements IProductionConfig {

	protected final List<CameraConfig> cameraConfigs = new ArrayList<CameraConfig>();
	protected final List<PrinterConfig> printerConfigs = new ArrayList<PrinterConfig>();
	protected BisConfig bisConfig;

	protected PlcConfig plcConfig;

	protected String authenticatorMode;
	protected String activationBehavior;

	public List<CameraConfig> getCameraConfigs() {
		return cameraConfigs;
	}

	public List<PrinterConfig> getPrinterConfigs() {
		return printerConfigs;
	}

	public PlcConfig getPlcConfig() {
		return plcConfig;
	}

	public void setPlcConfig(PlcConfig plcConfig) {
		this.plcConfig = plcConfig;
	}

	@Override
	public BisConfig getBisConfig() {
		return bisConfig;
	}

	public void setBisConfig(BisConfig bisConfig) {
		this.bisConfig = bisConfig;
	}

	@Override
	public String getAuthenticatorMode() {
		return authenticatorMode;
	}

	public void setAuthenticatorMode(String authenticatorMode) {
		this.authenticatorMode = authenticatorMode;
	}

	public String getActivationBehavior() {
		return activationBehavior;
	}

	public void setActivationBehavior(String activationBehavior) {
		this.activationBehavior = activationBehavior;
	}

}
