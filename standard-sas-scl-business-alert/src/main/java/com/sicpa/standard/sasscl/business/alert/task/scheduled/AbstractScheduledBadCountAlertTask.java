package com.sicpa.standard.sasscl.business.alert.task.scheduled;

import java.util.ArrayList;
import java.util.List;

/**
 * send an alert if the number of bad events is over a threshold
 * 
 * @author DIelsch
 * 
 */
public abstract class AbstractScheduledBadCountAlertTask extends AbstractScheduledAlertTask {

	protected List<Boolean> events = new ArrayList<Boolean>();

	public AbstractScheduledBadCountAlertTask() {
	}

	@Override
	protected boolean isAlertPresent() {
		synchronized (events) {
			if (events.size() >= getSampleSize()) {
				int counter = 0;
				for (Boolean event : events) {
					if (!event) {
						counter++;
					}
				}
				// remove the events that are over the sample size
				while (events.size() > getSampleSize()) {
					events.remove(0);
				}
				return counter >= getThreshold();
			}
		}
		return false;
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
		}
	}

	@Override
	public void reset() {
		events.clear();
	}

	public abstract int getThreshold();

	public abstract int getSampleSize();
}
