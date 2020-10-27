package com.sicpa.standard.sasscl.view.selection.select;

import com.sicpa.standard.sasscl.model.ProductionParameters;

public interface ISelectProductionParametersViewListener {

	void productionParametersSelected(ProductionParameters productionParameters);
	
	void goToProductionMode();

}
