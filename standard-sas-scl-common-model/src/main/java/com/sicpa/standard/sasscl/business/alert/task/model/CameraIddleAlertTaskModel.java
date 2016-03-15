package com.sicpa.standard.sasscl.business.alert.task.model;

public class CameraIddleAlertTaskModel extends AbstractAlertTaskModel {

	private int maxInactiveTimeInSec;
	private int delayInSec;

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