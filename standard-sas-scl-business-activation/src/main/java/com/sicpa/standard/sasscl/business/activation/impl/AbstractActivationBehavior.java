package com.sicpa.standard.sasscl.business.activation.impl;

import com.sicpa.standard.sasscl.business.activation.IActivationBehavior;
import com.sicpa.standard.sasscl.model.ProductionParameters;

public abstract class AbstractActivationBehavior implements IActivationBehavior {

	protected ProductionParameters productionParameters;

	public void setProductionParameters(ProductionParameters productionParameters) {
		this.productionParameters = productionParameters;
	}

	public ProductionParameters getProductionParameters() {
		return productionParameters;
	}
}