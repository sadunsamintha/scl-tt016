package com.sicpa.ttth.view.sku.batch;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.NoSuchElementException;
import java.util.Properties;
import javax.swing.JOptionPane;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.sasscl.devices.remote.impl.dtoConverter.DailyBatchRequestRepository;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.view.AbstractViewFlowController;
import com.sicpa.ttth.storage.TTTHFileStorage;
import com.sicpa.ttth.view.flow.TTTHDefaultScreensFlow;

import static com.sicpa.ttth.scl.utils.TTTHCalendarUtils.TH_YEAR_DIFF;
import static com.sicpa.ttth.view.flow.TTTHScreenFlowTriggers.BATCH_ID_TRANSITION;

public class BatchJobIdSkuViewController extends AbstractViewFlowController implements IBatchJobIdSkuListener {

	private static final Logger logger = LoggerFactory.getLogger(BatchJobIdSkuViewController.class);

	private TTTHDefaultScreensFlow screensFlow;
	private BatchJobIdSKUModel model;
	private BatchJobIdSkuView batchJobIdSkuView;
	private DailyBatchRequestRepository dailyBatchRequestRepository;

	private String siteCode;

	private int batchJobIdSize;
	private int batchJobSiteSize;
	private int batchJobSeqSize;

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
	public void generateBatchJobId(String batchJobSeq, String batchJobSkuId) {
		String lineID;

		try {
			lineID = getLineIDFromProp();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(batchJobIdSkuView, Messages.get("sku.batch.job.line.id.blank"));
			return;
		}

		String dateStr = getCurrentDateStr();

		if (StringUtils.isBlank(batchJobSeq) || StringUtils.isBlank(batchJobSkuId)) {
			JOptionPane.showMessageDialog(batchJobIdSkuView, Messages.get("sku.batch.id.validation.blank"));
			return;
		}

		if (batchJobSeq.length() > getBatchJobSeqSize()) {
			JOptionPane.showMessageDialog(batchJobIdSkuView, Messages.format("sku.batch.seq.validation.size", getBatchJobSeqSize()));
			return;
		}

		if (!StringUtils.isNumeric(batchJobSeq)) {
			JOptionPane.showMessageDialog(batchJobIdSkuView,Messages.get("sku.batch.id.validation.format"));
			return;
		}

		StringBuilder batchJobId = new StringBuilder();
		batchJobId.append(siteCode)
			.append("-")
			.append(lineID)
			.append("-")
			.append(dateStr)
			.append("-")
			.append(batchJobSeq)
			.append(batchJobSkuId)
			.append("-")
			.append("A");

		saveBatchJobId(batchJobId.toString());

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

	private String getLineIDFromProp() throws IOException  {
		//Get the line id during run time as it may change depending on line id service.
		Properties prop = new Properties();
		String lineID;

		prop.load(new FileInputStream(TTTHFileStorage.GLOBAL_PROPERTIES_PATH));
		lineID = prop.getProperty("subsystemId");

		return lineID;
	}

	private String getCurrentDateStr() {
		Calendar now = Calendar.getInstance();
		now.add(Calendar.YEAR, TH_YEAR_DIFF);
		return new SimpleDateFormat("ddMMyy").format(now.getTime());
	}

	public BatchJobIdSKUModel getModel() {
		return model;
	}

	public int getBatchJobIdSize() {
		return batchJobIdSize;
	}

	public int getBatchJobSiteSize() {
		return batchJobSiteSize;
	}

	public int getBatchJobSeqSize() {
		return batchJobSeqSize;
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

	public void setSiteCode(String siteCode) {
		this.siteCode = siteCode;
	}

	public void setBatchJobIdSize(int batchJobIdSize) {
		this.batchJobIdSize = batchJobIdSize;
	}

	public void setBatchJobSiteSize(int batchJobSiteSize) {
		this.batchJobSiteSize = batchJobSiteSize;
	}

	public void setBatchJobSeqSize(int batchJobSeqSize) {
		this.batchJobSeqSize = batchJobSeqSize;
	}

}
