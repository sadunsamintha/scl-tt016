package com.sicpa.standard.sasscl.controller.skuselection;

public interface ISkuSelectionBehavior {

	boolean isLoadPreviousSelection();

	boolean isAutomaticStartProductionAfterSelection();

	void stopProduction();

	void onError();

	void onDisconnection();

	void onProductionParameterChanged();

	void onProductReadButNoSelection();

}
