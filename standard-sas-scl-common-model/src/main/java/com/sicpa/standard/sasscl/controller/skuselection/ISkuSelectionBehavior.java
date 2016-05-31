package com.sicpa.standard.sasscl.controller.skuselection;

import com.sicpa.standard.sasscl.controller.ProductionParametersEvent;

public interface ISkuSelectionBehavior {

	boolean isLoadPreviousSelection();

	boolean isStartAutomaticWhenReady();

	void stopProduction();

	void onProductionParameterChanged(ProductionParametersEvent evt);

}
