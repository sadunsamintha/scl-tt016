package com.sicpa.standard.sasscl.business.alert.task.model;

public class CameraIddleAlertTaskModel {

	protected boolean enabled=true;
	protected int maxInactiveTimeInSec;
	protected int delayInSec;

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public int getMaxInactiveTimeInSec() {
		return maxInactiveTimeInSec;
	}

	public void setMaxInactiveTimeInSec(int maxInactiveTimeInSec) {
		this.maxInactiveTimeInSec = maxInactiveTimeInSec;
	}

	public void setDelayInSec(int delay) {
		this.delayInSec = delay;
	}

	public int getDelayInSec() {
		return delayInSec;
	}
}