package com.sicpa.standard.sasscl.provider.impl;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.provider.ProductBatchIdProvider;
import com.sicpa.tt065.event.BatchIdViewEvent;

/**
 * Overwriting of the ProductionBatchProvider class to manage the new BatchIdEvent with the productionParameters
 * variable
 *
 * @author mjimenez
 *
 */
public class TT065ProductionBatchProvider extends ProductionBatchProvider implements ProductBatchIdProvider {

	public TT065ProductionBatchProvider() {
		super();
	}

	@Subscribe
	public void handleSkuSelectionButtonPressed(BatchIdViewEvent evt){
		ProductionParameters pp = evt.getProductionParameters();
		String strBatchId = pp.getProperty(productionBatchId);
		set(strBatchId);

		logger.info("TT065ProductionBatchProvider,user=" + evt.user.getLogin() + ",date=" + evt.date.toString()
				+ ", batchId=" + strBatchId);
	}
}