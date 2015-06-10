package com.sicpa.standard.sasscl.controller.flow.statemachine.executor;

import com.sicpa.standard.client.common.statemachine.IStateAction;
import com.sicpa.standard.client.common.view.screensflow.IScreensFlow;
import com.sicpa.standard.sasscl.view.ScreensFlowTriggers;

public class ExecutorSelectWithPrevious implements IStateAction {
	protected IScreensFlow screensFlow;

	@Override
	public void enter() {
		screensFlow.moveToNext(ScreensFlowTriggers.REQUEST_SELECTION);
	}

	@Override
	public void leave() {

	}

	public void setScreensFlow(IScreensFlow screensFlow) {
		this.screensFlow = screensFlow;
	}
}
