package com.sicpa.standard.sasscl.controller.flow.statemachine.executor;

import com.sicpa.standard.client.common.statemachine.IStateAction;
import com.sicpa.standard.sasscl.controller.hardware.IHardwareController;

public class ExecutorDisconnectingOnParamChanged implements IStateAction {

	protected IHardwareController hardwareController;

	@Override
	public void enter() {
		hardwareController.disconnect();
	}

	public void setHardwareController(IHardwareController hardwareController) {
		this.hardwareController = hardwareController;
	}

	@Override
	public void leave() {

	}
}
