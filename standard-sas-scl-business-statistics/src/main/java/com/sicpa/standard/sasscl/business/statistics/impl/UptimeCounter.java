package com.sicpa.standard.sasscl.business.statistics.impl;

public class UptimeCounter {

	protected long startTime=-1;

	protected long stopTime=-1;

	public UptimeCounter() {

	}

	public void start() {
		stopTime = -1;
		startTime = System.currentTimeMillis();
	}

	public void stop() {
		if (stopTime < 0) {
			stopTime = System.currentTimeMillis();
		}
	}

	public void reset() {
		startTime = -1;
		stopTime = -1;
	}

	public int getUptime() {

		if (startTime < 0) {
			return 0;
		}

		if (stopTime < 0) {
			return (int) ((System.currentTimeMillis() - startTime) / 1000);
		}

		return (int) ((stopTime - startTime) / 1000);

	}
}