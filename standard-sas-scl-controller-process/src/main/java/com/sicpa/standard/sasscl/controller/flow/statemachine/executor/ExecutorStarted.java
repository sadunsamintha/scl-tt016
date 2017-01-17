package com.sicpa.standard.sasscl.controller.flow.statemachine.executor;

import com.sicpa.standard.client.common.statemachine.IStateAction;
import com.sicpa.standard.sasscl.business.alert.IAlert;

public class ExecutorStarted implements IStateAction {

	private IAlert alert;

	@Override
	public void enter() {
		alert.start();
	}

	@Override
	public void leave() {
	}

	public void setAlert(IAlert alert) {
		this.alert = alert;
	}
}
