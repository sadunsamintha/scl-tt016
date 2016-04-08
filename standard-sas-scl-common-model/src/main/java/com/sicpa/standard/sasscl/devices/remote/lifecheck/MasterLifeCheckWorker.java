package com.sicpa.standard.sasscl.devices.remote.lifecheck;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.sicpa.standard.client.common.utils.TaskExecutor;
import com.sicpa.standard.sasscl.devices.remote.connector.IConnectable;

public class MasterLifeCheckWorker implements IMasterLifeCheckWorker {

	private ScheduledFuture<?> scheduledTask;
	private int lifecheckIntervalSec;
	private IConnectable connector;

	public MasterLifeCheckWorker() {

	}

	@Override
	public void start() {
		if (scheduledTask == null) {
			scheduledTask = TaskExecutor.scheduleWithFixedDelay(() -> connector.isAlive(), lifecheckIntervalSec,
					TimeUnit.SECONDS, "MPCC-LifeChecker");
		}
	}

	@Override
	public void stop() {
		scheduledTask.cancel(true);
	}

	@Override
	public void scheduleReconnection() {
		TaskExecutor.schedule(() -> connector.doLogin(), lifecheckIntervalSec, TimeUnit.SECONDS, "MPCC-LifeChecker");
	}

	public void setLifecheckIntervalSec(int lifecheckIntervalSec) {
		this.lifecheckIntervalSec = lifecheckIntervalSec;
	}

	public void setConnector(IConnectable connector) {
		this.connector = connector;
	}
}