package com.sicpa.standard.sasscl.provider.impl;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.provider.ProductBatchIdExpDtProvider;
import com.sicpa.tt079.event.BatchIdExpViewEvent;

/**
 * Overwriting of the ProductionBatchProvider class to manage the new BatchIdEvent with the productionParameters
 * variable
 *
 *
 */
public class TT079ProductionBatchIdProvider extends ProductionBatchProvider implements ProductBatchIdExpDtProvider {

	public TT079ProductionBatchIdProvider() {
		super();
	}

	@Subscribe
	public void handleSkuSelectionButtonPressed(BatchIdExpViewEvent evt){
		ProductionParameters pp = evt.getProductionParameters();
		String strBatchId = pp.getProperty(productionBatchId);
		set(strBatchId);

		logger.info("TT079ProductionBatchProvider,user=" + evt.user.getLogin() + ",date=" + evt.date.toString()
				+ ", batchId=" + strBatchId);
	}
}