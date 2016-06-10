package com.sicpa.standard.sasscl.controller.skuselection.impl;

import com.sicpa.standard.sasscl.controller.ProductionParametersEvent;
import com.sicpa.standard.sasscl.controller.skuselection.ISkuSelectionBehavior;

public class FullOperatorSelectionBehavior implements ISkuSelectionBehavior {

	@Override
	public boolean isLoadPreviousSelection() {
		return true;
	}

	@Override
	public boolean isStartAutomaticWhenReady() {
		return false;
	}

	@Override
	public void duringProductionOnProductionParameterChanged(ProductionParametersEvent evt) {
		throw new IllegalStateException();
	}

}
