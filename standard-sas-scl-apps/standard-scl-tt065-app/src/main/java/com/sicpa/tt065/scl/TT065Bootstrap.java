package com.sicpa.tt065.scl;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.Bootstrap;
import com.sicpa.standard.sasscl.common.storage.IStorage;
import com.sicpa.standard.sasscl.controller.ProductionParametersEvent;
import com.sicpa.standard.sasscl.model.ProductStatus;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.model.statistics.StatisticsKey;
import com.sicpa.standard.sasscl.provider.impl.TT065ProductionBatchProvider;
import com.sicpa.tt065.model.TT065ProductStatus;
import com.sicpa.tt065.model.TT065ProductionMode;

import static com.sicpa.standard.sasscl.custoBuilder.CustoBuilder.*;
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


        addProductionModeRefeedStock();
        addProductStatusRefeedStockStatisticsKeyMapping();
        addProductStatusRefeedStockRemoteServerStatusMapping();
        addRefeedStockProductStatusToPackageSender();
    }

    private void addProductionModeRefeedStock() {
        //TODO 200 is the MARKET TYPE ID on the MSCL DB for production mode Refeed Stock
        addProductionMode(TT065ProductionMode.REFEED_STOCK, "refeedStock", 200);
    }

    private void addProductStatusRefeedStockRemoteServerStatusMapping() {
        //TODO I put the same 200 for the PRODUCT STATUS ID on the MSCL DB for refeed stock products
        addToRemoteMapping(TT065ProductStatus.REFEED_STOCK, 200);
    }

    private void addProductStatusRefeedStockStatisticsKeyMapping() {
        addToStatisticsMapper(TT065ProductStatus.REFEED_STOCK, StatisticsKey.GOOD);
    }

    private void addRefeedStockProductStatusToPackageSender() {
        setPackagerType(TT065ProductStatus.REFEED_STOCK, true);
    }

    private void mapProductStatusCountingToStatisticGood() {
        addToStatisticsMapper(ProductStatus.COUNTING, StatisticsKey.GOOD);
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
        addPropertyToClass(SKU.class, skuCompliant);
    }

    public void setStorage(IStorage storage) {
        this.storage = storage;
    }

    public void setProductionBatchProvider(TT065ProductionBatchProvider productionBatchProvider) {
        this.productionBatchProvider = productionBatchProvider;
    }
}
