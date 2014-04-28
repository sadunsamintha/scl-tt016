package com.sicpa.standard.sasscl.business.alert.task.scheduled;


/**
 * 
 * base class for alert task that can count good/bad events<br>
 * is executed periodically only if the sample size has been reached (meaning executed if good+bad>=sampleSize)
 * 
 * @author DIelsch
 */
public abstract class AbstractScheduledGoodBadAlertTask extends AbstractScheduledAlertTask {

	protected int good = 0;
	protected int bad = 0;

	public AbstractScheduledGoodBadAlertTask() {
	}

	protected void increaseGood() {
		this.good++;
	}

	protected void increaseBad() {
		this.bad++;
	}

	@Override
	public void reset() {
		this.bad = 0;
		this.good = 0;
	}

	public abstract int getThreshold();

	@Override
	public void checkForMessage() {
		if (this.good + this.bad >= getSampleSize()) {
			super.checkForMessage();
			reset();
		}
	}

	public abstract int getSampleSize();
}
