package com.sicpa.standard.sasscl.business.alert.task.model;

public class PlcActivationCounterCheckModel {
	protected int maxDelta;
	protected boolean enabled=true;

	public int getMaxDelta() {
		return maxDelta;
	}

	public void setMaxDelta(int maxDelta) {
		this.maxDelta = maxDelta;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}