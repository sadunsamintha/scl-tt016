package com.sicpa.standard.sasscl.controller;

import com.sicpa.standard.sasscl.model.ProductionParameters;

public class ProductionParametersEvent {
	private ProductionParameters productionParameters;

	public ProductionParametersEvent( ProductionParameters productionParameters) {
		this.productionParameters = productionParameters;
	}

	public ProductionParameters getProductionParameters() {
		return this.productionParameters;
	}
}
