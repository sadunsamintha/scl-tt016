package com.sicpa.standard.sasscl.controller.skuselection.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.controller.ProductionParametersEvent;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.controller.skuselection.ISkuSelectionBehavior;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.provider.impl.UnknownSkuProvider;

public class SkuSelectionDuringProduction implements ISkuSelectionBehavior {

	private static final Logger logger = LoggerFactory.getLogger(SkuSelectionDuringProduction.class);

	private ProductionParameters productionParameters;
	private UnknownSkuProvider unknownSkuProvider;

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
		SKU sku = evt.getProductionParameters().getSku();
		logger.info("setting sku:" + sku);
		productionParameters.setSku(evt.getProductionParameters().getSku());
		EventBusService.post(evt);
	}

	@Subscribe
	public void handleApplicationStateChanged(ApplicationFlowStateChangedEvent evt) {
		if (evt.getCurrentState().equals(ApplicationFlowState.STT_CONNECTING)) {
			productionParameters.setSku(unknownSkuProvider.get());
			EventBusService.post(new ProductionParametersEvent(productionParameters));
		}
	}

	public void setProductionParameters(ProductionParameters productionParameters) {
		this.productionParameters = productionParameters;
	}

	public void setUnknownSkuProvider(UnknownSkuProvider unknownSkuProvider) {
		this.unknownSkuProvider = unknownSkuProvider;
	}
}
