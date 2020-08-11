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

public class BatchJobIdSkuViewController extends AbstractViewFlowController implements IBatchJobIdSkuListener {

	private static final Logger logger = LoggerFactory.getLogger(BatchJobIdSkuViewController.class);

	private TTTHDefaultScreensFlow screensFlow;
	private BatchJobIdSKUModel model;
	private int batchJobIdSize;

	public BatchJobIdSkuViewController(){
		this(new BatchJobIdSKUModel());
	}

	public BatchJobIdSkuViewController(BatchJobIdSKUModel model){
		this.model = model;
	}

	public void setScreensFlow(TTTHDefaultScreensFlow screensFlow) {
		this.screensFlow = screensFlow;
	}

	@Override
	public void saveBatchJobId(String strBatchJobId) {

		if (StringUtils.isBlank(strBatchJobId)){
			JOptionPane.showMessageDialog(null, Messages.get("sku.batch.id.validation.blank"));
			return;
		}

		if (!StringUtils.isNumeric(strBatchJobId)){
			JOptionPane.showMessageDialog(null,Messages.get("sku.batch.id.validation.format"));
			return;
		}
		
		if (strBatchJobId.length() > this.getBatchJobIdSize()){
			JOptionPane.showMessageDialog(null, Messages.format("sku.batch.id.validation.size",this.getBatchJobIdSize()));
			return;
		}

		model.setStrBatchJobId(strBatchJobId);
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
		BatchJobIdSkuView view = (BatchJobIdSkuView) this.view;
		view.refresh();
	}

	public BatchJobIdSKUModel getModel() {
		return model;
	}

	public int getBatchJobIdSize() {
		return batchJobIdSize;
	}

	public void setBatchJobIdSize(int batchJobIdSize) {
		this.batchJobIdSize = batchJobIdSize;
	}

}
