package com.sicpa.standard.sasscl.devices.remote;

public class GlobalMonitoringToolInfo {

	@Override
	public String toString() {
		return "GlobalMonitoringToolInfo [productionStarted=" + productionStarted + "]";
	}

	protected boolean productionStarted;

	public boolean isProductionStarted() {
		return productionStarted;
	}

	public void setProductionStarted(boolean productionStarted) {
		this.productionStarted = productionStarted;
	}
}
