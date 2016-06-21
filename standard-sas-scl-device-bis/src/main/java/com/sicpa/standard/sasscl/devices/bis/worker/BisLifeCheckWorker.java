package com.sicpa.standard.sasscl.devices.bis.worker;

import static com.sicpa.standard.client.common.utils.TaskExecutor.scheduleAtFixedRate;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.ScheduledFuture;

import com.sicpa.standard.sasscl.devices.bis.IBisController;

public class BisLifeCheckWorker {

	private ScheduledFuture<?> scheduledTask;
	private int lifecheckIntervalSec;
	private IBisController controller;

	private boolean connected = false;

	public BisLifeCheckWorker(int lifecheckIntervalSec, IBisController controller) {
		this.lifecheckIntervalSec = lifecheckIntervalSec;
		this.controller = controller;
	}

	public void start() {
		if (scheduledTask == null) {
			scheduledTask = scheduleAtFixedRate(() -> checkConnection(), 0, lifecheckIntervalSec, SECONDS,
					"BIS-LifeChecker");
		}
	}

	public void stop() {
		scheduledTask.cancel(true);
	}

	private void checkConnection() {
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

	public void setLifecheckIntervalSec(int lifecheckIntervalSec) {
		this.lifecheckIntervalSec = lifecheckIntervalSec;
	}

}
