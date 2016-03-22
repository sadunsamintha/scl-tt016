package com.sicpa.standard.sasscl.business.alert.task.model;

public class CameraCountAlertTaskModel extends AbstractAlertTaskModel {

	private int maxUnreadCount;
	private int sampleSize;
	private int delayInSec;

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