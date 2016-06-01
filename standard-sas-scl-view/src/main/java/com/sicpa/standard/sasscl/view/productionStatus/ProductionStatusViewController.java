package com.sicpa.standard.sasscl.view.productionStatus;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.utils.listener.CoalescentPeriodicListener;
import com.sicpa.standard.sasscl.business.activation.NewProductEvent;
import com.sicpa.standard.sasscl.view.AbstractViewFlowController;

public class ProductionStatusViewController extends AbstractViewFlowController implements
        IProductionStatusViewListener {

    private boolean enable;

    private final CoalescentPeriodicListener newProductEventConcentrator = new CoalescentPeriodicListener(500,
            () -> fireProgressBarChangedChanged());


    public ProductionStatusViewModel getModel() {
        return model;
    }

    private ProductionStatusViewModel model;

    public ProductionStatusViewController() {
        this(new ProductionStatusViewModel());
    }

    public ProductionStatusViewController(ProductionStatusViewModel model) {
        this.model = model;
    }


    @Subscribe
    public void handleNewProductEvent(NewProductEvent evt) {
        if (!enable)
            return;

        newProductEventConcentrator.eventReceived();
    }

    private void fireProgressBarChangedChanged() {
        model.notifyModelChanged();
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }


}
