package com.sicpa.ttth.view.sku.batch;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Properties;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.sasscl.devices.remote.impl.dtoConverter.DailyBatchRequestRepository;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.view.AbstractViewFlowController;
import com.sicpa.ttth.remote.utils.ChecksumUtil;
import com.sicpa.ttth.storage.TTTHFileStorage;
import com.sicpa.ttth.view.flow.TTTHDefaultScreensFlow;

import static com.sicpa.ttth.view.flow.TTTHScreenFlowTriggers.BATCH_ID_TRANSITION;

public class BatchJobIdSkuViewController extends AbstractViewFlowController implements IBatchJobIdSkuListener {

	private static final Logger logger = LoggerFactory.getLogger(BatchJobIdSkuViewController.class);

	private TTTHDefaultScreensFlow screensFlow;
	private BatchJobIdSKUModel model;
	private BatchJobIdSkuView batchJobIdSkuView;
	private DailyBatchRequestRepository dailyBatchRequestRepository;

	public BatchJobIdSkuViewController(){
		this(new BatchJobIdSKUModel());
	}

	public BatchJobIdSkuViewController(BatchJobIdSKUModel model){
		this.model = model;
	}

	@Override
	public void saveBatchJobId(String strBatchJobId) {
		try {
			SKU sku = dailyBatchRequestRepository.getDailyBatchSKU(strBatchJobId);
			model.setStrBatchJobId(strBatchJobId);
			dailyBatchRequestRepository.updateStatistics(strBatchJobId);
			EventBusService.post(getModel());
			EventBusService.post(sku);
		} catch (NoSuchElementException e) {
			logger.error("Sku not available for " + strBatchJobId, e);
			JOptionPane.showMessageDialog(batchJobIdSkuView, Messages.get("sku.batch.sku.mismatch"));
		}
	}

	@Override
	public void saveBatchJobHist(String strBatchJobId) {
		model.setStrBatchJobId(strBatchJobId);
		EventBusService.post(getModel());
		screensFlow.moveToNext(BATCH_ID_TRANSITION);
	}

	@Override
	public void generateBatchJobId(String batchJobId) {
		try {
			initChecksumUtil();
			if (!ChecksumUtil.validateCheckSum(batchJobId)) {
				JOptionPane.showMessageDialog(batchJobIdSkuView, Messages.get("sku.daily.batch.checksum.invalid"));
				return;
			}
			saveBatchJobId(batchJobId);
		} catch (IOException e) {
			logger.error("Failed to initialize checksum util. ", e);
		}
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

	private void initChecksumUtil() throws IOException {
		Properties prop = new Properties();
		int startingShift;
		int modular;

		prop.load(new FileInputStream(TTTHFileStorage.GLOBAL_PROPERTIES_PATH));
		startingShift = Integer.parseInt(prop.getProperty("batch.checksum.startingShift"));
		modular = Integer.parseInt(prop.getProperty("batch.checksum.modular"));

		ChecksumUtil.initialize(startingShift, modular);
	}

	public BatchJobIdSKUModel getModel() {
		return model;
	}

	public void setScreensFlow(TTTHDefaultScreensFlow screensFlow) {
		this.screensFlow = screensFlow;
	}

	public void setBatchJobIdSkuView(BatchJobIdSkuView batchJobIdSkuView) {
		this.batchJobIdSkuView = batchJobIdSkuView;
	}

	public void setDailyBatchRequestRepository(DailyBatchRequestRepository dailyBatchRequestRepository) {
		this.dailyBatchRequestRepository = dailyBatchRequestRepository;
	}

}
