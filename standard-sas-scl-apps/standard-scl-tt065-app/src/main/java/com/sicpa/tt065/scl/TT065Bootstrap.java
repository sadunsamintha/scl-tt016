package com.sicpa.tt065.scl;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.Bootstrap;
import com.sicpa.standard.sasscl.common.storage.IStorage;
import com.sicpa.standard.sasscl.controller.ProductionParametersEvent;
import com.sicpa.standard.sasscl.custoBuilder.CustoBuilder;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.provider.ProductBatchIdProvider;

import static com.sicpa.tt065.model.TT065CustomProperties.skuCompliant;

/**
 * Created by wvieira on 15/09/2016.
 */
public class TT065Bootstrap extends Bootstrap implements ProductBatchIdProvider {

    static {
        addPropertyCompliantToSku();
    }

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
            EventBusService.post(new ProductionParametersEvent(previous));
        }
    }

    private static void addPropertyCompliantToSku() {
        CustoBuilder.addPropertyToClass(SKU.class, skuCompliant);
    }

    public void setStorage(IStorage storage) {
        this.storage = storage;
    }
}
