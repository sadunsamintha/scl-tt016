package com.sicpa.standard.sasscl.devices.bis.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.common.util.ThreadUtils;
import com.sicpa.standard.common.util.lifechecker.LifeCheckWorker;
import com.sicpa.standard.sasscl.devices.bis.IBisController;
import com.sicpa.standard.sasscl.devices.bis.IScheduleWorker;

public abstract class AbstractScheduleWorker implements IScheduleWorker {

	protected boolean mWorking = true;

	protected Thread mWorker;

	protected long mScheduleInterval;

	/**
	 * this is used for pausing the life checker
	 */
	protected boolean mRunning;

	private static final Logger logger = LoggerFactory.getLogger(LifeCheckWorker.class);

	protected IBisController controller;

	protected String workerName;

	public AbstractScheduleWorker(String workerName, long mScheduleInterval) {
		this.workerName = workerName;
		this.mScheduleInterval = mScheduleInterval;
	}

	public void create() {
		if (this.mWorker == null || (!this.mWorker.isAlive())) {
			logger.debug("AbstractScheduleWorker | Creating schedule worker : " + this.workerName);
			mWorker = new Thread(new Worker(), this.workerName);
			mWorker.start();
			mWorking = true;
		} else {
			logger.debug(this.workerName + " is already created");
		}
	}

	/**
	 * to start the life checker
	 */
	@Override
	public void start() {
		if (!this.mRunning) {
			mRunning = true;
			// wake up thread
			this.mWorker.interrupt();
		} else {
			logger.debug(this.workerName + " is already started");
		}
	}

	/**
	 * calling the LifeCheckWorker join method
	 * 
	 */
	protected void join() {
		if (mWorker != null) {
			try {
				this.mWorker.interrupt();
				if (this.mWorker.isAlive()) {
					this.mWorker.join();
				}
			} catch (InterruptedException e) {
			}
		}
	}

	/**
	 * Stop the life check worker.
	 */
	@Override
	public void stop() {
		this.mRunning = false;
	}

	public void dispose() {

		if (this.mWorker == null || (!this.mWorker.isAlive())) {
			return;
		}

		this.mWorking = false;
		this.mRunning = false;
		// wait till thread die before exiting the method
		this.join();
	}

	protected class Worker implements Runnable {

		// ~ Implementation of Runnable
		// --------------------------------------------

		public void run() {

			while (mWorking) {

				ThreadUtils.sleepQuietly(mScheduleInterval);

				while (mRunning) {
					try {
						doWork();
					} catch (Exception e) {
						logger.error("Failed connect: " + e.getMessage());
					}
					ThreadUtils.sleepQuietly(mScheduleInterval);
				}
			}
		}
	}

	protected abstract void doWork();

	public void setScheduleInterval(long mScheduleInterval) {
		this.mScheduleInterval = mScheduleInterval;
	}

	@Override
	public void addController(IBisController controller) {
		this.controller = controller;
	}

}
