package com.sicpa.standard.sasscl.controller.flow.statemachine.executor;

import com.sicpa.standard.client.common.statemachine.IStateAction;
import com.sicpa.standard.sasscl.business.alert.IAlert;
import com.sicpa.standard.sasscl.controller.hardware.IHardwareController;

public class ExecutorStopping implements IStateAction {

	protected IAlert alert;

	protected IHardwareController hardwareController;

	@Override
	public void enter() {
		alert.stop();
		hardwareController.stop();
	}

	@Override
	public void leave() {

	}

	public void setHardwareController(IHardwareController hardwareController) {
		this.hardwareController = hardwareController;
	}

	public void setAlert(IAlert alert) {
		this.alert = alert;
	}
}
