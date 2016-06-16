package com.sicpa.standard.sasscl.devices.bis.worker;

public class ConnectionLifeCheckWorker extends AbstractScheduleWorker {

	private boolean connected = false;

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
