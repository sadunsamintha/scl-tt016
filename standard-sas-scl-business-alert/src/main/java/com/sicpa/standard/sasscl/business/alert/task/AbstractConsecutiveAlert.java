package com.sicpa.standard.sasscl.business.alert.task;

import java.util.concurrent.atomic.AtomicInteger;


public abstract class AbstractConsecutiveAlert extends AbstractAlertTask {

	protected int threshold;
	protected final AtomicInteger events = new AtomicInteger(0);

	@Override
	protected boolean isAlertPresent() {
		return events.get() >= threshold;
	}

	@Override
	public void reset() {
		events.set(0);
	}

	public void increase() {
		events.incrementAndGet();
		checkForMessage();
	}

	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}

	public int getThreshold() {
		return threshold;
	}
}
