package com.sicpa.standard.sasscl.business.alert.task.scheduled;

/**
 * send an alert if the percentage of bad events is over a threshold
 * 
 * @author DIelsch
 * 
 */
public abstract class AbstractScheduledPercentageAlertTask extends AbstractScheduledGoodBadAlertTask {

	public AbstractScheduledPercentageAlertTask() {
	}

	@Override
	protected boolean isAlertPresent() {
		if (bad + good == 0) {
			return false;
		}
		return (((float) bad) / (bad + good)) * 100 > getThreshold();
	}
}
