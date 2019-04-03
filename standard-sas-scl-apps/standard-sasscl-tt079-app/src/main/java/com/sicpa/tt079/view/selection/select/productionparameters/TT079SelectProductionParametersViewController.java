package com.sicpa.tt079.view.selection.select.productionparameters;

import com.sicpa.standard.sasscl.common.log.OperatorLogger;
import com.sicpa.standard.sasscl.controller.flow.IFlowControl;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.view.ScreensFlowTriggers;
import com.sicpa.standard.sasscl.view.selection.select.SelectProductionParametersViewController;
import com.sicpa.tt079.view.flow.TT079ScreenFlowTriggers;

/**
 * Overwritting class of the SelectProductionParametersViewController to manage
 * the productionParameterSelected with the next screen flow
 *
 */
public class TT079SelectProductionParametersViewController extends SelectProductionParametersViewController {

	@Override
	public void productionParametersSelected(ProductionParameters pp) {
		mainFrameController.setSku(pp.getSku());
		mainFrameController.setProductionMode(pp.getProductionMode());
		mainFrameController.setBarcode(pp.getBarcode());

		OperatorLogger.log("Product Mode: {}", pp.getProductionMode().getDescription());
		if (pp.getSku() != null) {
			OperatorLogger.log("Product Param: {}", pp.getSku().getDescription());
		}
		screensFlow.moveToNext(ScreensFlowTriggers.PRODUCTION_PARAMETER_SELECTED);
	}
	
	@Override
	protected void userChanged() {
		if(isNoSelectionState()) {
			screensFlow.moveToNext(TT079ScreenFlowTriggers.BACK_TO_SELECTION);
			super.userChanged();
		}
	}

}
