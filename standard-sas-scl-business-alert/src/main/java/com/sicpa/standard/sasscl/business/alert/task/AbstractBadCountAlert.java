package com.sicpa.standard.sasscl.business.alert.task;

import java.util.ArrayList;
import java.util.List;


public abstract class AbstractBadCountAlert extends AbstractAlertTask {

	protected int threshold;
	protected int sampleSize;
	protected final List<Boolean> events = new ArrayList<Boolean>();

	public AbstractBadCountAlert() {

	}

	@Override
	public void reset() {
		events.clear();
	}

	@Override
	protected boolean isAlertPresent() {
		synchronized (events) {
			int counter = 0;
			for (Boolean event : events) {
				if (!event) {
					counter++;
				}
			}
			return counter >= threshold;
		}
	}

	public void increaseGood() {
		increase(true);
	}

	public void increaseBad() {
		increase(false);
	}

	public void increase(boolean event) {
		synchronized (events) {
			events.add(event);
			while (events.size() > sampleSize) {
				events.remove(0);
			}
		}
		checkForMessage();
	}

	public void setSampleSize(int sampleSize) {
		this.sampleSize = sampleSize;
	}

	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}

	public int getThreshold() {
		return threshold;
	}

	public int getSampleSize() {
		return sampleSize;
	}

}
