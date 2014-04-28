package com.sicpa.standard.sasscl.devices.brs;

public class BrsConfigBean {

	protected int differenceCountThreshold;
	protected int unreadThreshold;
	protected int unreadWindowSize;

	public BrsConfigBean() {
	}

	public int getDifferenceCountThreshold() {
		return differenceCountThreshold;
	}

	public void setDifferenceCountThreshold(int differenceCountThreshold) {
		this.differenceCountThreshold = differenceCountThreshold;
	}

	public int getUnreadThreshold() {
		return unreadThreshold;
	}

	public void setUnreadThreshold(int unreadThreshold) {
		this.unreadThreshold = unreadThreshold;
	}

	public int getUnreadWindowSize() {
		return unreadWindowSize;
	}

	public void setUnreadWindowSize(int unreadWindowSize) {
		this.unreadWindowSize = unreadWindowSize;
	}

}
