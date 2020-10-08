package com.sicpa.ttth.view.sku.barcode;

import com.google.common.eventbus.Subscribe;
import javax.swing.JOptionPane;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.view.AbstractViewFlowController;
import com.sicpa.ttth.view.flow.TTTHDefaultScreensFlow;

import static com.sicpa.ttth.messages.TTTHMessageEventKey.SKUSELECTION.BARCODE_VERIFIED;
import static com.sicpa.ttth.view.flow.TTTHScreenFlowTriggers.BARCODE_TRANSITION;

public class BarcodeSkuViewController extends AbstractViewFlowController implements IBarcodeSkuListener {

	private static final Logger logger = LoggerFactory.getLogger(BarcodeSkuViewController.class);

	private TTTHDefaultScreensFlow screensFlow;
	private BarcodeSkuModel model;

	public BarcodeSkuViewController(){
		this(new BarcodeSkuModel());
	}

	public BarcodeSkuViewController(BarcodeSkuModel model){
		this.model = model;
	}

	public void setScreensFlow(TTTHDefaultScreensFlow screensFlow) {
		this.screensFlow = screensFlow;
	}

	@Override
	public void saveBarcode(String strBarcode) {

		if (StringUtils.isBlank(strBarcode)){
			JOptionPane.showMessageDialog(null, Messages.get("sku.barcode.validation.blank"));
			return;
		}

		if (!StringUtils.isNumeric(strBarcode)){
			JOptionPane.showMessageDialog(null,Messages.get("sku.barcode.validation.format"));
			return;
		}
		
		if (strBarcode.length() != model.getSkuBarcodes().get(0).length()){
			JOptionPane.showMessageDialog(null, Messages.format("sku.barcode.validation.size",
				model.getSkuBarcodes().get(0).length()));
			return;
		}

		if (!model.getSkuBarcodes().contains(strBarcode)) {
			JOptionPane.showMessageDialog(null, Messages.format("sku.barcode.validation.mismatch"));
			return;
		}

		EventBusService.post(new MessageEvent(BARCODE_VERIFIED));
	}

	@Override
	public void returnToSelection() {
		screensFlow.moveToNext(BARCODE_TRANSITION);
	}

	@Override
	protected void displayView() {
		super.displayView();
		BarcodeSkuView view = (BarcodeSkuView) this.view;
		view.refresh();
	}

	@Subscribe
	public void getBarcodeFromSKU(BarcodeSkuModel evt) {
		this.model.setSkuBarcodes(evt.getSkuBarcodes());
		this.model.setSkuName(evt.getSkuName());
		model.notifyModelChanged();
	}

	public BarcodeSkuModel getModel() {
		return model;
	}

}
