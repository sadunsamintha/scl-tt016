package com.sicpa.tt080.scl.provider.impl;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.provider.ProductBatchIdProvider;
import com.sicpa.standard.sasscl.provider.impl.ProductionBatchProvider;
import com.sicpa.tt080.scl.event.BatchIdViewEvent;

/**
 * Overwriting of the ProductionBatchProvider class to manage the new BatchIdEvent with the productionParameters
 * variable
 *
 * @author mjimenez
 *
 */
public class TT080ProductionBatchProvider extends ProductionBatchProvider implements ProductBatchIdProvider {

	public TT080ProductionBatchProvider() {
		super();
	}

	@Subscribe
	public void handleSkuSelectionButtonPressed(BatchIdViewEvent evt){
		ProductionParameters pp = evt.getProductionParameters();
		String strBatchId = pp.getProperty(productionBatchId).getProductionBatchId();
		set(strBatchId);

		logger.info("TT080ProductionBatchProvider,user={}, date={}, batchId={}", evt.user.getLogin(), evt.date.toString(), strBatchId);
	}
}