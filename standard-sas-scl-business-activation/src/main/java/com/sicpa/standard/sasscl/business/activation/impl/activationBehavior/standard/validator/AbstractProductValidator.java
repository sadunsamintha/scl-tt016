package com.sicpa.standard.sasscl.business.activation.impl.activationBehavior.standard.validator;

import com.sicpa.standard.sasscl.model.ProductionParameters;

public abstract class AbstractProductValidator implements IProductValidator {

	protected ProductionParameters productionParameters;

	public void setProductionParameters(ProductionParameters productionParameters) {
		this.productionParameters = productionParameters;
	}

	public ProductionParameters getProductionParameters() {
		return productionParameters;
	}

}