package com.sicpa.standard.sasscl.controller.skuselection;

import com.sicpa.standard.sasscl.controller.ProductionParametersEvent;

public interface ISkuSelectionBehavior {

	/**
	 * return true if when the application is starting the previous Sku selection should be loaded
	 */
	boolean isLoadPreviousSelection();

	/**
	 * return true if when everything is ready the production should start
	 */
	boolean isStartAutomaticWhenReady();

	/**
	 * called when a production mode/sku changed is detected during production
	 */
	void duringProductionOnProductionParameterChanged(ProductionParametersEvent evt);

}
