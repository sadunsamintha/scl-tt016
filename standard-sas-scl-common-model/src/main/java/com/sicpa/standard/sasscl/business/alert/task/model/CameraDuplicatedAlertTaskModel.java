package com.sicpa.standard.sasscl.business.alert.task.model;

public class CameraDuplicatedAlertTaskModel {

	protected boolean enabled=true;
	protected int threshold;

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public int getThreshold() {
		return threshold;
	}

	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}
}