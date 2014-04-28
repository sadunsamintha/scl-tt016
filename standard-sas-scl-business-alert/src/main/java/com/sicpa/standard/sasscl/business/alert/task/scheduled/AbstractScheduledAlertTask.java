package com.sicpa.standard.sasscl.business.alert.task.scheduled;

import java.util.Timer;
import java.util.TimerTask;

import com.sicpa.standard.sasscl.business.alert.task.AbstractAlertTask;

/**
 * define the implementation for a task that should be executed periodically
 * 
 * @author DIelsch
 */
public abstract class AbstractScheduledAlertTask extends AbstractAlertTask {

	protected Timer timer;

	public AbstractScheduledAlertTask() {
	}

	public void setTimer(Timer timer) {
		this.timer = timer;
	}

	@Override
	public void start() {
		if (getDelay() <= 0) {
			return;
		}

		if (null == timer)
			createNewTimer();

		try {
			scheduleAtFixedRate();
		} catch (IllegalStateException e) {

			// timer has been cancelled
			createNewTimer();
			scheduleAtFixedRate();
		}
	}

	protected void createNewTimer() {
		timer = new Timer();
	}

	protected void scheduleAtFixedRate() {
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				checkForMessage();
			}
		}, getDelay(), getDelay());
	}

	@Override
	public void stop() {
		if (timer != null) {
			timer.cancel();
			timer.purge();
		}
	}

	public abstract long getDelay();
}
