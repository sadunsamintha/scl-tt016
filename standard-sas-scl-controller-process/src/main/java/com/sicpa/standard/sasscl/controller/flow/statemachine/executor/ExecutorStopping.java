package com.sicpa.standard.sasscl.controller.flow.statemachine.executor;

import com.sicpa.standard.sasscl.business.alert.IAlert;
import com.sicpa.standard.sasscl.controller.hardware.IHardwareController;

public class ExecutorStopping implements Runnable {

	protected IAlert alert;

	protected IHardwareController hardwareController;

	@Override
	public void run() {
		alert.stop();
		hardwareController.stop();
	}

	public void setHardwareController(IHardwareController hardwareController) {
		this.hardwareController = hardwareController;
	}

	public void setAlert(IAlert alert) {
		this.alert = alert;
	}
}
