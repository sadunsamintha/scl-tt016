package com.sicpa.standard.sasscl.view.selection.display;

import com.sicpa.standard.client.common.view.mvc.AbstractObservableModel;
import com.sicpa.standard.sasscl.model.ProductionParameters;

public class SelectionDisplayModel extends AbstractObservableModel {

	protected ProductionParameters productionParameters;

	public void setProductionParameters(ProductionParameters productionParameters) {
		this.productionParameters = productionParameters;
	}

	public ProductionParameters getProductionParameters() {
		return productionParameters;
	}

}
