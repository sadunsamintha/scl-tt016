package com.sicpa.ttth.scl;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.Bootstrap;
import com.sicpa.standard.sasscl.controller.ProductionParametersEvent;
import com.sicpa.standard.sasscl.custoBuilder.CustoBuilder;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.provider.ProductBatchJobIdProvider;

import static com.sicpa.standard.sasscl.messages.ActionMessageType.WARNING;
import static com.sicpa.ttth.messages.TTTHMessageEventKey.SKUSELECTION.BARCODE_VERIFIED;
import static com.sicpa.ttth.messages.TTTHMessageEventKey.SKUSELECTION.BARCODE_VERIFIED_MSG_CODE;

public class TTTHBootstrap extends Bootstrap implements ProductBatchJobIdProvider {

	@Override
	public void executeSpringInitTasks(){
		super.executeSpringInitTasks();
		addSkuSelectionMessages();
	}

	@Override
	protected void restorePreviousSelectedProductionParams() {
		ProductionParameters previous = storage.getSelectedProductionParameters();
		if (productionParametersValidator.validate(previous)) {
			productionParameters.setBarcode(previous.getBarcode());
			productionParameters.setSku(previous.getSku());
			productionParameters.setProductionMode(previous.getProductionMode());
			productionParameters.setProperty(productionBatchJobId, previous.getProperty(productionBatchJobId));
			EventBusService.post(new ProductionParametersEvent(previous));
		}
	}

	private void addSkuSelectionMessages() {
		CustoBuilder.addMessage(BARCODE_VERIFIED, BARCODE_VERIFIED_MSG_CODE, WARNING);
	}
}
