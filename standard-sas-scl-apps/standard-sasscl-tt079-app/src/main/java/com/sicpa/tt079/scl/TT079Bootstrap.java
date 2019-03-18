package com.sicpa.tt079.scl;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.Bootstrap;
import com.sicpa.standard.sasscl.controller.ProductionParametersEvent;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.provider.impl.TT079ProductionBatchIdProvider;

import static com.sicpa.standard.sasscl.provider.ProductBatchIdExpDtProvider.productionBatchId;
import static com.sicpa.standard.sasscl.provider.ProductBatchIdExpDtProvider.productionExpdt;

public class TT079Bootstrap extends Bootstrap {
	
	private TT079ProductionBatchIdProvider productionBatchProvider;

	@Override
	public void executeSpringInitTasks() {
		super.executeSpringInitTasks();
	}
	
	@Override
    protected void restorePreviousSelectedProductionParams() {
        ProductionParameters previous = storage.getSelectedProductionParameters();
        if (productionParametersValidator.validate(previous)) {
            productionParameters.setBarcode(previous.getBarcode());
            productionParameters.setSku(previous.getSku());
            productionParameters.setProductionMode(previous.getProductionMode());
            productionParameters.setProperty(productionBatchId, previous.getProperty(productionBatchId));
            productionParameters.setProperty(productionExpdt, previous.getProperty(productionExpdt));
            EventBusService.post(new ProductionParametersEvent(previous));

            productionBatchProvider.set(previous.getProperty(productionBatchId));
        }
    }

	public void setProductionBatchProvider(TT079ProductionBatchIdProvider productionBatchProvider) {
		this.productionBatchProvider = productionBatchProvider;
	}
	
	
}