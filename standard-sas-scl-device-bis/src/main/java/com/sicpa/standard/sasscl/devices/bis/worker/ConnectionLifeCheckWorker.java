package com.sicpa.standard.sasscl.devices.bis.worker;

public class ConnectionLifeCheckWorker extends AbstractScheduleWorker {
	protected boolean connected = false;

	/**
	 * constructor
	 * 
	 * @param scheduleInterval
	 */
	public ConnectionLifeCheckWorker(long scheduleInterval) {
		super("BIS Connection Life Check Worker", scheduleInterval);
	}

	@Override
	protected void doWork() {
		if (!controller.isConnected()) {
			controller.onLifeCheckFailed();
			connected = false;
		} else {
			if (!connected) {
				controller.onLifeCheckSucceed();
			}
			connected = true;
		}

		controller.sendLifeCheck();
	}

}
