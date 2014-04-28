package com.sicpa.standard.sasscl.business.alert.task.model;

public class CameraCountAlertTaskModel {

	protected boolean enabled = true;
	protected int maxUnreadCount;
	protected int sampleSize;
	protected int delayInSec;

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public int getMaxUnreadCount() {
		return maxUnreadCount;
	}

	public void setMaxUnreadCount(int maxUnreadCount) {
		this.maxUnreadCount = maxUnreadCount;
	}

	public int getSampleSize() {
		return sampleSize;
	}

	public void setSampleSize(int sampleSize) {
		this.sampleSize = sampleSize;
	}

	public void setDelayInSec(int delay) {
		this.delayInSec = delay;
	}

	public int getDelay() {
		return delayInSec;
	}
}