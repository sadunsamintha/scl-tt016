package com.sicpa.tt085.view.selection.select;

import com.sicpa.standard.sasscl.common.log.OperatorLogger;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.view.ScreensFlowTriggers;
import com.sicpa.standard.sasscl.view.selection.select.SelectProductionParametersViewController;
import com.sicpa.tt085.model.provider.CountryProvider;

public class TT085SelectProductionParametersViewController extends SelectProductionParametersViewController implements CountryProvider{ 
	
	@Override
	public void productionParametersSelected(ProductionParameters pp) {
		mainFrameController.setSku(pp.getSku());
		mainFrameController.setProductionMode(pp.getProductionMode());
		mainFrameController.setBarcode(pp.getBarcode());
		((TT085MainFrameController)mainFrameController).setCountry(pp.getProperty(country));
		
		OperatorLogger.log("Product Mode: {}", pp.getProductionMode().getDescription());
		if (pp.getSku() != null) {
			OperatorLogger.log("Product Param: {}", pp.getSku().getDescription());
		}

		mainFrameController.productionParametersChanged();

		screensFlow.moveToNext(ScreensFlowTriggers.PRODUCTION_PARAMETER_SELECTED);
	}

}
