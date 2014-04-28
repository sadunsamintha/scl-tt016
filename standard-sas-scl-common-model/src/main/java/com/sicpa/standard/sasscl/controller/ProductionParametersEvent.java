package com.sicpa.standard.sasscl.controller;

import com.sicpa.standard.sasscl.model.ProductionParameters;

public class ProductionParametersEvent {
	protected ProductionParameters productionParameters;

	public ProductionParametersEvent(final ProductionParameters productionParameters) {
		this.productionParameters = productionParameters;
	}

	public ProductionParameters getProductionParameters() {
		return this.productionParameters;
	}
}
