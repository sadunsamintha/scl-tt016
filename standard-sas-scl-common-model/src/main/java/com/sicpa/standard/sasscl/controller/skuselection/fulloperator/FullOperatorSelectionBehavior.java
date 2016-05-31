package com.sicpa.standard.sasscl.controller.skuselection.fulloperator;

import com.sicpa.standard.sasscl.controller.ProductionParametersEvent;
import com.sicpa.standard.sasscl.controller.flow.IFlowControl;
import com.sicpa.standard.sasscl.controller.skuselection.ISkuSelectionBehavior;

public class FullOperatorSelectionBehavior implements ISkuSelectionBehavior {

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
	public void onProductionParameterChanged(ProductionParametersEvent evt) {
		throw new IllegalStateException();
	}

	public void setFlowControl(IFlowControl flowControl) {
		this.flowControl = flowControl;
	}
}
