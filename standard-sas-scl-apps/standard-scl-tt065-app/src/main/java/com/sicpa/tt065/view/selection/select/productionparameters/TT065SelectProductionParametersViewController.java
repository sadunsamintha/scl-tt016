package com.sicpa.tt065.view.selection.select.productionparameters;

import com.sicpa.standard.sasscl.common.log.OperatorLogger;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.view.ScreensFlowTriggers;
import com.sicpa.standard.sasscl.view.selection.select.SelectProductionParametersViewController;

/**
 * Overwritting class of the SelectProductionParametersViewController to manage
 * the productionParameterSelected with the next screen flow
 *
 * @author mjimenez
 *
 */
public class TT065SelectProductionParametersViewController extends SelectProductionParametersViewController {

	@Override
	public void productionParametersSelected(ProductionParameters pp) {
		mainFrameController.setSku(pp.getSku());
		mainFrameController.setProductionMode(pp.getProductionMode());
		mainFrameController.setBarcode(pp.getBarcode());

		OperatorLogger.log("Product Mode: {}", pp.getProductionMode().getDescription());
		if (pp.getSku() != null) {
			OperatorLogger.log("Product Param: {}", pp.getSku().getDescription());
		}

		//mainFrameController.productionParametersChanged();

		screensFlow.moveToNext(ScreensFlowTriggers.PRODUCTION_PARAMETER_SELECTED);
	}


}
