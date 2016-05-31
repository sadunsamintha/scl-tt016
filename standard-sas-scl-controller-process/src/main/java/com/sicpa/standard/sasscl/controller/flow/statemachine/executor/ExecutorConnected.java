package com.sicpa.standard.sasscl.controller.flow.statemachine.executor;

import com.sicpa.standard.client.common.statemachine.IStateAction;
import com.sicpa.standard.sasscl.controller.flow.IFlowControl;
import com.sicpa.standard.sasscl.controller.skuselection.ISkuSelectionBehavior;

public class ExecutorConnected implements IStateAction {

	private IFlowControl flowControl;
	private ISkuSelectionBehavior skuSelectionBehavior;

	@Override
	public void enter() {
		if (skuSelectionBehavior.isStartAutomaticWhenReady()) {
			flowControl.notifyStartProduction();
		}
	}

	@Override
	public void leave() {

	}

	public void setFlowControl(IFlowControl flowControl) {
		this.flowControl = flowControl;
	}

	public void setSkuSelectionBehavior(ISkuSelectionBehavior skuSelectionBehavior) {
		this.skuSelectionBehavior = skuSelectionBehavior;
	}
}
