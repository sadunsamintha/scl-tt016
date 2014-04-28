package com.sicpa.standard.sasscl.view.productionSending;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.sasscl.business.production.IProduction;
import com.sicpa.standard.sasscl.model.ProductionSendingProgress;
import com.sicpa.standard.sasscl.view.AbstractViewFlowController;

public class ProductionSendingViewController extends AbstractViewFlowController implements
		IProductionSendingViewListener {

	protected IProduction production;

	protected ProductionSendingViewModel model;

	public ProductionSendingViewController() {
		this(new ProductionSendingViewModel());
	}

	public ProductionSendingViewController(ProductionSendingViewModel model) {
		this.model = model;
	}

	@Override
	public void requestCancel() {
		production.cancelSending();
	}

	@Subscribe
	public void handleProgressChanged(ProductionSendingProgress evt) {
		model.setBatchCount(evt.getBatchCount());
		model.setCurrentBatch(evt.getCurrentBatchIndex());
		model.notifyModelChanged();
	}

	public void setProduction(IProduction production) {
		this.production = production;
	}

	public ProductionSendingViewModel getModel() {
		return model;
	}
}
