package com.sicpa.ttth.view.selection.select;

import com.google.common.eventbus.Subscribe;
import java.util.Objects;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.custoBuilder.CustoBuilder;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionParameterRootNode;
import com.sicpa.standard.sasscl.provider.ProductBatchJobIdProvider;
import com.sicpa.standard.sasscl.view.ScreensFlowTriggers;
import com.sicpa.standard.sasscl.view.selection.select.ISelectProductionParametersView;
import com.sicpa.standard.sasscl.view.selection.select.SelectProductionParametersViewController;
import com.sicpa.ttth.view.flow.TTTHScreenFlowTriggers;
import com.sicpa.ttth.view.sku.barcode.BarcodeSkuModel;
import com.sicpa.ttth.view.sku.batch.BatchJobIdSKUModel;

import static com.sicpa.ttth.messages.TTTHMessageEventKey.SKUSELECTION.BARCODE_VERIFIED;

public class TTTHSelectProductionParametersViewController extends SelectProductionParametersViewController
	implements ProductBatchJobIdProvider {

	private boolean isBatchIDSet = false;
	private String strBatchJobId = "";

	private ProductionParameters pp;

	public TTTHSelectProductionParametersViewController() {
		CustoBuilder.addPropertyToClass(ProductionParameters.class, productionBatchJobId);
	}

	@Override
	public void productionParametersSelected(ProductionParameters pp) {
		if (pp.getSku() == null) {
			if (pp.getProductionMode() == ProductionMode.EXPORT) {
				this.pp.setProductionMode(pp.getProductionMode());
				showExportSKUList();
			} else if (pp.getProductionMode() != ProductionMode.MAINTENANCE) {
				this.pp.setProductionMode(pp.getProductionMode());
				screensFlow.moveToNext(TTTHScreenFlowTriggers.STANDARD_MODE_TRANSITION);
			} else {
				super.productionParametersSelected(pp);
				screensFlow.moveToNext(ScreensFlowTriggers.PRODUCTION_PARAMETER_SELECTED);
			}
		} else {
			if (mainFrameController.getProductionMode() == ProductionMode.EXPORT) {
				this.pp.setSku(pp.getSku());
				this.pp.setBarcode(pp.getBarcode());
				addSkuDetailsToMainFrame();
				mainFrameController.productionParametersChanged();
				screensFlow.moveToNext(ScreensFlowTriggers.PRODUCTION_PARAMETER_SELECTED);
			} else {
				this.pp.setSku(pp.getSku());
				this.pp.setBarcode(pp.getBarcode());
				this.pp.setProperty(productionBatchJobId, strBatchJobId);
				EventBusService.post(new BarcodeSkuModel(this.pp.getSku().getDescription(), this.pp.getSku().getBarCodes()));
				isBatchIDSet = false;
                addSkuDetailsToMainFrame();
                screensFlow.moveToNext(TTTHScreenFlowTriggers.BARCODE_TRANSITION);
            }
		}
	}

	private void addSkuDetailsToMainFrame() {
		mainFrameController.setSku(this.pp.getSku());
		mainFrameController.setProductionMode(this.pp.getProductionMode());
		mainFrameController.setBarcode(this.pp.getBarcode());
	}

	@Override
	protected void displayView() {
		if (isBatchIDSet) {
			showStandardSKUList();
		} else {
			super.displayView();
		}
	}

	public void showExportSKUList() {
		super.displayView();
		ProductionParameterRootNode exportRootNode = new ProductionParameterRootNode();
		exportRootNode.getChildren().addAll(Objects.requireNonNull(skuListProvider.get().getChildren().stream()
			.filter(node -> Messages.get("productionmode.export").equals(node.getText()))
			.findAny()
			.orElse(null)).getChildren());
		((ISelectProductionParametersView) getComponent()).displaySelectionScreen((exportRootNode));
	}

	public void showStandardSKUList() {
		super.displayView();
		ProductionParameterRootNode standardRootNode = new ProductionParameterRootNode();
		standardRootNode.getChildren().addAll(Objects.requireNonNull(skuListProvider.get().getChildren().stream()
			.filter(node -> Messages.get("productionmode.standard").equals(node.getText()))
			.findAny()
			.orElse(null)).getChildren());
		((ISelectProductionParametersView) getComponent()).displaySelectionScreen((standardRootNode));
	}

	@Subscribe
	public void handleBatchIDSet(BatchJobIdSKUModel evt) {
		isBatchIDSet = true;
		strBatchJobId = evt.getStrBatchJobId();
	}

	@Subscribe
	public void handleBarcodeVerified(MessageEvent evt) {
		if (evt.getKey().equals(BARCODE_VERIFIED)) {
			mainFrameController.setProductionParameters(this.pp);
			mainFrameController.productionParametersChanged();
			screensFlow.moveToNext(ScreensFlowTriggers.PRODUCTION_PARAMETER_SELECTED);
		}
	}

	@Subscribe
	public void handleAutoSKUSelection(SKU evt) {
		ProductionParameters pp = new ProductionParameters();
		pp.setSku(evt);
		productionParametersSelected(pp);
	}

	@Override
	protected void userChanged() {
		if(isNoSelectionState()) {
			isBatchIDSet = false;
			screensFlow.moveToNext(TTTHScreenFlowTriggers.BATCH_ID_TRANSITION);
			super.userChanged();
		}
	}

	public void setPp(ProductionParameters pp) {
		this.pp = pp;
	}
}
