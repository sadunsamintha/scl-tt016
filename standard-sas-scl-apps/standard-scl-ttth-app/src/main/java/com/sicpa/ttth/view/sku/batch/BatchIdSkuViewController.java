package com.sicpa.ttth.view.sku.batch;

import javax.swing.JOptionPane;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.sasscl.view.AbstractViewFlowController;
import com.sicpa.ttth.view.flow.TTTHDefaultScreensFlow;

import static com.sicpa.ttth.view.flow.TTTHScreenFlowTriggers.BATCH_ID_TRANSITION;

public class BatchIdSkuViewController extends AbstractViewFlowController implements IBatchIdSkuListener {

	private static final Logger logger = LoggerFactory.getLogger(BatchIdSkuViewController.class);

	private TTTHDefaultScreensFlow screensFlow;
	private BatchIdSKUModel model;
	private int batchIdSize;

	public BatchIdSkuViewController(){
		this(new BatchIdSKUModel());
	}

	public BatchIdSkuViewController(BatchIdSKUModel model){
		this.model = model;
	}

	public void setScreensFlow(TTTHDefaultScreensFlow screensFlow) {
		this.screensFlow = screensFlow;
	}

	@Override
	public void saveBatchId(String strBatchId) {

		if (StringUtils.isBlank(strBatchId)){
			JOptionPane.showMessageDialog(null, Messages.get("sku.batch.id.validation.blank"));
			return;
		}

		if (!StringUtils.isNumeric(strBatchId)){
			JOptionPane.showMessageDialog(null,Messages.get("sku.batch.id.validation.format"));
			return;
		}
		
		if (strBatchId.length() > this.getBatchIdSize()){
			JOptionPane.showMessageDialog(null, Messages.format("sku.batch.id.validation.size",this.getBatchIdSize()));
			return;
		}

		model.setStrBatchId(strBatchId);
		EventBusService.post(getModel());
		screensFlow.moveToNext(BATCH_ID_TRANSITION);
	}

	@Override
	public void returnToSelection() {
		screensFlow.moveToNext(BATCH_ID_TRANSITION);
	}

	@Override
	protected void displayView() {
		super.displayView();
		BatchIdSkuView view = (BatchIdSkuView) this.view;
		view.refresh();
	}

	public BatchIdSKUModel getModel() {
		return model;
	}

	public int getBatchIdSize() {
		return batchIdSize;
	}

	public void setBatchIdSize(int batchIdSize) {
		this.batchIdSize = batchIdSize;
	}

}
