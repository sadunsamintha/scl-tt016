package com.sicpa.tt016.view.selection;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.common.log.OperatorLogger;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.view.ScreensFlowTriggers;
import com.sicpa.standard.sasscl.view.selection.select.ISelectProductionParametersView;
import com.sicpa.standard.sasscl.view.selection.select.SelectProductionParametersViewController;
import com.sicpa.tt016.devices.plc.PlcPersistentGrossNetProductCounterManager;
import com.sicpa.tt016.devices.plc.PlcPersistentProductCounterManager;

import static com.sicpa.tt016.messages.TT016MessageEventKey.AUTOMATEDBEAM.SKU_SELECTION_VIEW_ACTIVE;

public class TT016SelectProductionParametersViewControllerSAS extends SelectProductionParametersViewController {

    private PlcPersistentProductCounterManager plcPersistentProductCounterManager;
    private PlcPersistentGrossNetProductCounterManager plcPersistentGrossNetProductCounterManager;

    private ProductionParameters productionParameters;

    @Override
    public void productionParametersSelected(ProductionParameters pp) {
        if (isSkuChanged(pp.getSku())) {
            plcPersistentProductCounterManager.execute();
            plcPersistentGrossNetProductCounterManager.updateProductParamAndCount();
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

    @Override
    protected void displayView() {
        super.displayView();
        ((ISelectProductionParametersView) getComponent()).displaySelectionScreen(skuListProvider.get());
        EventBusService.post(new MessageEvent(SKU_SELECTION_VIEW_ACTIVE));
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

    public void setPlcPersistentGrossNetProductCounterManager(
			PlcPersistentGrossNetProductCounterManager plcPersistentGrossNetProductCounterManager) {
		this.plcPersistentGrossNetProductCounterManager = plcPersistentGrossNetProductCounterManager;
	}

	public void setProductionParameters(ProductionParameters productionParameters) {
        this.productionParameters = productionParameters;
    }
}