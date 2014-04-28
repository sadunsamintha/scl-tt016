package com.sicpa.standard.sasscl.controller.flow.statemachine.executor;

import com.sicpa.standard.client.common.view.screensflow.IScreensFlow;
import com.sicpa.standard.sasscl.view.ScreensFlowTriggers;

public class ExecutorSelectWithPrevious implements Runnable {
	protected IScreensFlow screensFlow;

	@Override
	public void run() {
		screensFlow.moveToNext(ScreensFlowTriggers.REQUEST_SELECTION);
	}

	public void setScreensFlow(IScreensFlow screensFlow) {
		this.screensFlow = screensFlow;
	}
}
