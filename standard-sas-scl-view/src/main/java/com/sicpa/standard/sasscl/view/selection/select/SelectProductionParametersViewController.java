package com.sicpa.standard.sasscl.view.selection.select;

import javax.swing.JComponent;

import com.sicpa.standard.client.common.view.screensflow.IScreensFlow;
import com.sicpa.standard.sasscl.common.log.OperatorLogger;
import com.sicpa.standard.sasscl.config.GlobalBean;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.provider.impl.SkuListProvider;
import com.sicpa.standard.sasscl.view.AbstractViewFlowController;
import com.sicpa.standard.sasscl.view.MainFrameController;
import com.sicpa.standard.sasscl.view.ScreensFlowTriggers;

public class SelectProductionParametersViewController extends AbstractViewFlowController implements
		ISelectProductionParametersViewListener {

	protected ISelectProductionParametersView handPickingview;
	protected ISelectProductionParametersView barcodeView;
	protected GlobalBean globalBean;

	protected SkuListProvider skuListProvider;

	protected MainFrameController mainFrameController;

	protected IScreensFlow screensFlow;

	public SelectProductionParametersViewController() {
	}

	public void setSkuListProvider(SkuListProvider skuListProvider) {
		this.skuListProvider = skuListProvider;
	}

	public void setGlobalBean(GlobalBean globalBean) {
		this.globalBean = globalBean;
	}

	@Override
	public void productionParametersSelected(ProductionParameters pp) {
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
	public JComponent getComponent() {
		if (globalBean.isUseBarcodeReader()) {
			return (JComponent) barcodeView;
		} else {
			return (JComponent) handPickingview;
		}
	}

	public void setHandPickingview(ISelectProductionParametersView handPickingview) {
		this.handPickingview = handPickingview;
	}

	public void setBarcodeView(ISelectProductionParametersView barcodeView) {
		this.barcodeView = barcodeView;
	}

	@Override
	protected void displayView() {
		super.displayView();
		((ISelectProductionParametersView) getComponent()).displaySelectionScreen(skuListProvider.get());
	}

	public void setScreensFlow(IScreensFlow screensFlow) {
		this.screensFlow = screensFlow;
	}

	public void setMainFrameController(MainFrameController mainFrameController) {
		this.mainFrameController = mainFrameController;
	}
}
