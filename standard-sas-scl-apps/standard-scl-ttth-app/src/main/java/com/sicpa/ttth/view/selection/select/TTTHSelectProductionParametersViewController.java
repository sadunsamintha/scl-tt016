package com.sicpa.ttth.view.selection.select;

import com.google.common.eventbus.Subscribe;
import java.util.Objects;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.custoBuilder.CustoBuilder;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionParameterRootNode;
import com.sicpa.standard.sasscl.provider.ProductBatchIdProvider;
import com.sicpa.standard.sasscl.view.ScreensFlowTriggers;
import com.sicpa.standard.sasscl.view.selection.select.ISelectProductionParametersView;
import com.sicpa.standard.sasscl.view.selection.select.SelectProductionParametersViewController;
import com.sicpa.ttth.view.flow.TTTHScreenFlowTriggers;
import com.sicpa.ttth.view.sku.barcode.BarcodeSkuModel;
import com.sicpa.ttth.view.sku.batch.BatchIdSKUModel;

import static com.sicpa.ttth.messages.TTTHMessageEventKey.SKUSELECTION.BARCODE_VERIFIED;

public class TTTHSelectProductionParametersViewController extends SelectProductionParametersViewController
	implements ProductBatchIdProvider {

	private boolean isBatchIDSet = false;
	private String strBatchId = "";

	@Override
	public void productionParametersSelected(ProductionParameters pp) {
		if (pp.getSku() == null) {
			if (pp.getProductionMode() == ProductionMode.EXPORT) {
				mainFrameController.setProductionMode(pp.getProductionMode());
				showExportSKUList();
			} else if (pp.getProductionMode() != ProductionMode.MAINTENANCE) {
				mainFrameController.setProductionMode(pp.getProductionMode());
				screensFlow.moveToNext(TTTHScreenFlowTriggers.STANDARD_MODE_TRANSITION);
				CustoBuilder.addPropertyToClass(ProductionParameters.class, productionBatchId);
			} else {
				addSkuDetailsToMainFrame(pp);
				mainFrameController.productionParametersChanged();
				screensFlow.moveToNext(ScreensFlowTriggers.PRODUCTION_PARAMETER_SELECTED);
			}
		} else {
			if (mainFrameController.getProductionMode() == ProductionMode.EXPORT) {
				pp.setProductionMode(mainFrameController.getProductionMode());
				addSkuDetailsToMainFrame(pp);
				mainFrameController.productionParametersChanged();
				screensFlow.moveToNext(ScreensFlowTriggers.PRODUCTION_PARAMETER_SELECTED);
			} else {
                EventBusService.post(new BarcodeSkuModel(pp.getSku().getBarCodes()));
                pp.setProductionMode(mainFrameController.getProductionMode());
                pp.setProperty(productionBatchId, strBatchId);
                isBatchIDSet = false;
                addSkuDetailsToMainFrame(pp);
                screensFlow.moveToNext(TTTHScreenFlowTriggers.BARCODE_TRANSITION);
            }
		}
	}

	private void addSkuDetailsToMainFrame(ProductionParameters productionParameters) {
		mainFrameController.setSku(productionParameters.getSku());
		mainFrameController.setProductionMode(productionParameters.getProductionMode());
		mainFrameController.setBarcode(productionParameters.getBarcode());
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
	public void handleBatchIDSet(BatchIdSKUModel evt) {
		isBatchIDSet = true;
		strBatchId = evt.getStrBatchId();
	}

	@Subscribe
	public void handleBarcodeVerified(MessageEvent evt) {
		if (evt.getKey().equals(BARCODE_VERIFIED)) {
			mainFrameController.productionParametersChanged();
			screensFlow.moveToNext(ScreensFlowTriggers.PRODUCTION_PARAMETER_SELECTED);
		}
	}

	@Override
	protected void userChanged() {
		if(isNoSelectionState()) {
			isBatchIDSet = false;
			screensFlow.moveToNext(TTTHScreenFlowTriggers.BATCH_ID_TRANSITION);
			super.userChanged();
		}
	}

}
