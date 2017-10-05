package com.sicpa.tt016.view.selection;

import com.sicpa.standard.sasscl.common.log.OperatorLogger;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.view.ScreensFlowTriggers;
import com.sicpa.standard.sasscl.view.selection.select.SelectProductionParametersViewController;
import com.sicpa.tt016.devices.plc.PlcPersistentProductCounterManager;

public class TT016SelectProductionParametersViewController extends SelectProductionParametersViewController {

    private PlcPersistentProductCounterManager plcPersistentProductCounterManager;

    private ProductionParameters productionParameters;

    @Override
    public void productionParametersSelected(ProductionParameters pp) {
        if (isSkuChanged(pp.getSku())) {
            plcPersistentProductCounterManager.execute();
        }

        mainFrameController.setSku(pp.getSku());
        mainFrameController.setProductionMode(pp.getProductionMode());
        mainFrameController.setBarcode(pp.getBarcode());

        OperatorLogger.log("Product Mode: {}", pp.getProductionMode().getDescription());
        if (pp.getSku() != null) {
            OperatorLogger.log("Product Param: {}", pp.getSku().getDescription());
        }

        mainFrameController.productionParametersChanged();

        screensFlow.moveToNext(ScreensFlowTriggers.PRODUCTION_PARAMETER_SELECTED);
    }

    private boolean isSkuChanged(SKU sku) {
        if (productionParameters.getSku() == null) {
            return true;
        }

        return !productionParameters.getSku().equals(sku);
    }

    public void setPlcPersistentProductCounterManager(PlcPersistentProductCounterManager plcPersistentProductCounterManager) {
        this.plcPersistentProductCounterManager = plcPersistentProductCounterManager;
    }

    public void setProductionParameters(ProductionParameters productionParameters) {
        this.productionParameters = productionParameters;
    }
}