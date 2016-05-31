package com.sicpa.standard.sasscl.controller.skuselection.fulloperator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.sasscl.controller.flow.IFlowControl;
import com.sicpa.standard.sasscl.controller.skuselection.ISkuSelectionBehavior;

public class FullOperatorSelectionBehavior implements ISkuSelectionBehavior {

	private static final Logger logger = LoggerFactory.getLogger(FullOperatorSelectionBehavior.class);

	private IFlowControl flowControl;

	@Override
	public boolean isLoadPreviousSelection() {
		return true;
	}

	@Override
	public boolean isAutomaticStartProductionAfterSelection() {
		return false;
	}

	@Override
	public void stopProduction() {
		flowControl.notifyStopProduction();
	}

	@Override
	public void onError() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDisconnection() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProductionParameterChanged() {
		throw new IllegalStateException();
	}

	@Override
	public void onProductReadButNoSelection() {
		throw new IllegalStateException();
	}

	public void setFlowControl(IFlowControl flowControl) {
		this.flowControl = flowControl;
	}
}
