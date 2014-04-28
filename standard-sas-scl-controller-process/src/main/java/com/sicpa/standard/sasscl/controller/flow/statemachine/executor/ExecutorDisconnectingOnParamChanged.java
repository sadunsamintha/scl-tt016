package com.sicpa.standard.sasscl.controller.flow.statemachine.executor;

import com.sicpa.standard.sasscl.controller.hardware.IHardwareController;

public class ExecutorDisconnectingOnParamChanged implements Runnable {

	protected IHardwareController hardwareController;

	@Override
	public void run() {
		hardwareController.disconnect();
	}

	public void setHardwareController(IHardwareController hardwareController) {
		this.hardwareController = hardwareController;
	}
}
