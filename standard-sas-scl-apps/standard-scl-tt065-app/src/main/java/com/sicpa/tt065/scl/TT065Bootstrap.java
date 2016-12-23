package com.sicpa.tt065.scl;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.view.screensflow.ScreenTransition;
import com.sicpa.standard.sasscl.Bootstrap;
import com.sicpa.standard.sasscl.common.storage.IStorage;
import com.sicpa.standard.sasscl.controller.ProductionParametersEvent;
import com.sicpa.standard.sasscl.custoBuilder.CustoBuilder;
import com.sicpa.standard.sasscl.devices.plc.PlcUtils;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.provider.ProductBatchIdProvider;
import com.sicpa.standard.sasscl.view.LanguageSwitchEvent;
import com.sicpa.standard.sasscl.view.main.MainPanelGetter;
import com.sicpa.standard.sasscl.view.selection.select.SelectProductionParametersViewController;
import com.sicpa.tt065.event.BatchIdViewEvent;
import com.sicpa.tt065.view.sku.batchId.BatchIdSkuViewController;

import java.util.HashMap;
import java.util.Map;

import static com.sicpa.standard.sasscl.custoBuilder.CustoBuilder.addPlcVariable;
import static com.sicpa.standard.sasscl.custoBuilder.CustoBuilder.addScreen;
import static com.sicpa.standard.sasscl.custoBuilder.CustoBuilder.addScreenTransitions;
import static com.sicpa.standard.sasscl.view.ScreensFlowTriggers.PRODUCTION_PARAMETER_SELECTED;
import static com.sicpa.tt065.view.TT065ScreenFlowTriggers.BATCH_ID_REGISTERED;

/**
 * Created by wvieira on 15/09/2016.
 */
public class TT065Bootstrap extends Bootstrap implements ProductBatchIdProvider {

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

    public void setStorage(IStorage storage) {
        this.storage = storage;
    }
}
