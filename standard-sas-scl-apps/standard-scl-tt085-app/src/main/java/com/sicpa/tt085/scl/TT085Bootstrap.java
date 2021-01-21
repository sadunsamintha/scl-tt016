package com.sicpa.tt085.scl;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.Bootstrap;
import com.sicpa.standard.sasscl.controller.ProductionParametersEvent;
import com.sicpa.standard.sasscl.custoBuilder.CustoBuilder;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.tt085.model.provider.CountryProvider;

public class TT085Bootstrap extends Bootstrap implements CountryProvider {

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
            CustoBuilder.addPropertyToClass(ProductionParameters.class, country );
            productionParameters.setProperty(country, previous.getProperty(country));
            restoreStatistics();
            EventBusService.post(new ProductionParametersEvent(previous));
        }
    }

}
		