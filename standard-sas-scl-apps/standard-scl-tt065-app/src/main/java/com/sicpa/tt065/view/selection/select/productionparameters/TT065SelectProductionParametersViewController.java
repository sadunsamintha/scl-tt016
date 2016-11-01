package com.sicpa.tt065.view.selection.select.productionparameters;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.view.selection.select.SelectProductionParametersViewController;
import com.sicpa.tt065.event.BatchIdViewEvent;

import javax.swing.*;

import static com.sicpa.tt065.view.TT065ScreenFlowTriggers.PRODUCTION_PARAMETER_TO_BATCH;

public class TT065SelectProductionParametersViewController extends SelectProductionParametersViewController {

	@Override
	public void productionParametersSelected(ProductionParameters pp) {
		super.productionParametersSelected(pp);
		EventBusService.post(new BatchIdViewEvent(pp));
	}


}
