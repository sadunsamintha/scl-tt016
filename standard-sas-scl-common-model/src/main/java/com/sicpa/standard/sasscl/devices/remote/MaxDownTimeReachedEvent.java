package com.sicpa.standard.sasscl.devices.remote;

public class MaxDownTimeReachedEvent {

	boolean maxDownTimeReached;

	public MaxDownTimeReachedEvent(boolean maxDownTimeReached) {
		this.maxDownTimeReached = maxDownTimeReached;
	}

	public boolean isMaxDownTimeReached() {
		return maxDownTimeReached;
	}
}
