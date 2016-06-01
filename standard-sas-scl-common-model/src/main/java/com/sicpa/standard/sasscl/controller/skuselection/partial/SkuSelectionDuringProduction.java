package com.sicpa.standard.sasscl.controller.skuselection.partial;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.controller.ProductionParametersEvent;
import com.sicpa.standard.sasscl.controller.skuselection.ISkuSelectionBehavior;
import com.sicpa.standard.sasscl.model.ProductionParameters;

public class SkuSelectionDuringProduction implements ISkuSelectionBehavior {
	
	private ProductionParameters productionParameters;

	@Override
	public boolean isLoadPreviousSelection() {
		return false;
	}

	@Override
	public boolean isStartAutomaticWhenReady() {
		return false;
	}

	@Override
	public void duringProductionOnProductionParameterChanged(ProductionParametersEvent evt) {
		productionParameters.setSku(evt.getProductionParameters().getSku());
		EventBusService.post(evt);
	}

	public void setProductionParameters(ProductionParameters productionParameters) {
		this.productionParameters = productionParameters;
	}

}
