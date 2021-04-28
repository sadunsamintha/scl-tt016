package com.sicpa.standard.sasscl.view.selection.select;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.security.ILoginListener;
import com.sicpa.standard.client.common.security.SecurityService;
import com.sicpa.standard.client.common.view.screensflow.IScreensFlow;
import com.sicpa.standard.sasscl.common.log.OperatorLogger;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.provider.impl.SkuListProvider;
import com.sicpa.standard.sasscl.view.AbstractViewFlowController;
import com.sicpa.standard.sasscl.view.MainFrameController;
import com.sicpa.standard.sasscl.view.ScreensFlowTriggers;

public class SelectProductionParametersViewController extends AbstractViewFlowController implements
		ISelectProductionParametersViewListener {

	protected ISelectProductionParametersView handPickingView;
	protected ISelectProductionParametersView barcodeView;
	protected boolean useBarcodeReader;

	protected SkuListProvider skuListProvider;

	protected MainFrameController mainFrameController;

	protected IScreensFlow screensFlow;
	
	protected boolean noSelectionState;

	public SelectProductionParametersViewController() {
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
		if (useBarcodeReader) {
			return (JComponent) barcodeView;
		} else {
			return (JComponent) handPickingView;
		}
	}

	public void setHandPickingView(ISelectProductionParametersView handPickingView) {
		this.handPickingView = handPickingView;
	}

	public void setBarcodeView(ISelectProductionParametersView barcodeView) {
		this.barcodeView = barcodeView;
	}


	@Override
	protected void displayView() {
		addLoginListener();
		super.displayView();
		((ISelectProductionParametersView) getComponent()).displaySelectionScreen(skuListProvider.get());
	}

	public void setScreensFlow(IScreensFlow screensFlow) {
		this.screensFlow = screensFlow;
	}

	public void setMainFrameController(MainFrameController mainFrameController) {
		this.mainFrameController = mainFrameController;
	}
	public void setSkuListProvider(SkuListProvider skuListProvider) {
		this.skuListProvider = skuListProvider;
	}

	public void setUseBarcodeReader(boolean useBarcodeReader) {
		this.useBarcodeReader = useBarcodeReader;
	}
	
	private void addLoginListener() {
		SecurityService.addLoginListener(new ILoginListener() {
			@Override
			public void loginSucceeded(String login) {
				fireUserChanged();
			}

			@Override
			public void logoutCompleted(String login) {
				fireUserChanged();
			}
		});
	}
	
	private void fireUserChanged() {
		SwingUtilities.invokeLater(() -> userChanged());
	}
	
	protected void userChanged() {
		if(isNoSelectionState()) {
			super.displayView();
			((ISelectProductionParametersView) getComponent()).displaySelectionScreen(skuListProvider.get());
		}
	}
	
	@Subscribe
	public void handleApplicationFlowStateChangeEvent(ApplicationFlowStateChangedEvent event) {
		if (event.getCurrentState().equals(ApplicationFlowState.STT_SELECT_NO_PREVIOUS) || event.getCurrentState().equals(ApplicationFlowState.STT_SELECT_WITH_PREVIOUS)) {
			this.setNoSelectionState(true);
		} else {
			this.setNoSelectionState(false);
		}
	}

	public boolean isNoSelectionState() {
		return noSelectionState;
	}

	public void setNoSelectionState(boolean noSelectionState) {
		this.noSelectionState = noSelectionState;
	}

	@Override
	public void goToProductionMode() {
		setUseBarcodeReader(false);
		getComponent();
		displayView();
		//set back to original value
		//after select SKU view
		setUseBarcodeReader(true);
	}

	@Override
	public void selectionCanceled(){
		mainFrameController.productionParametersChanged();
		screensFlow.moveToNext(ScreensFlowTriggers.REQUEST_SELECTION_CANCEL);
	}
}
