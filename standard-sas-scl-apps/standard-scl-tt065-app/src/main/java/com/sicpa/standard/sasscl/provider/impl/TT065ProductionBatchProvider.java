package com.sicpa.standard.sasscl.provider.impl;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.tt065.event.BatchIdViewEvent;

import static com.sicpa.tt065.view.TT065ScreenFlowTriggers.PRODUCTION_PARAMETER_SELECTED;

public class TT065ProductionBatchProvider extends ProductionBatchProvider {

	public TT065ProductionBatchProvider() {
		super();
	}

	@Subscribe
	public void handleSkuSelectionButtonPressed(BatchIdViewEvent evt){
		ProductionParameters pp = evt.getPp();
		set("987654321");
		logger.info("TT065ProductionBatchProvider,user=" + evt.user.getLogin() + ",date=" + evt.date.toString() + ", batchId=");
	}


}