package com.sicpa.tt065.scl;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.Bootstrap;
import com.sicpa.standard.sasscl.common.storage.IStorage;
import com.sicpa.standard.sasscl.controller.ProductionParametersEvent;
import com.sicpa.standard.sasscl.custoBuilder.CustoBuilder;
import com.sicpa.standard.sasscl.model.ProductStatus;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.model.statistics.StatisticsKey;
import com.sicpa.standard.sasscl.provider.impl.TT065ProductionBatchProvider;

import static com.sicpa.standard.sasscl.provider.ProductBatchIdProvider.productionBatchId;
import static com.sicpa.tt065.model.TT065CustomProperties.skuCompliant;

/**
 * Created by wvieira on 15/09/2016.
 */
public class TT065Bootstrap extends Bootstrap {

    private TT065ProductionBatchProvider productionBatchProvider;

    static {
        addPropertyCompliantToSku();
    }

    @Override
    public void executeSpringInitTasks() {
        super.executeSpringInitTasks();
        mapProductStatusCountingToStatisticGood();
    }

    private void mapProductStatusCountingToStatisticGood() {
        CustoBuilder.addToStatisticsMapper(ProductStatus.COUNTING, StatisticsKey.GOOD);
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

            productionBatchProvider.set(previous.getProperty(productionBatchId));
        }
    }

    private static void addPropertyCompliantToSku() {
        CustoBuilder.addPropertyToClass(SKU.class, skuCompliant);
    }

    public void setStorage(IStorage storage) {
        this.storage = storage;
    }

    public void setProductionBatchProvider(TT065ProductionBatchProvider productionBatchProvider) {
        this.productionBatchProvider = productionBatchProvider;
    }
}
