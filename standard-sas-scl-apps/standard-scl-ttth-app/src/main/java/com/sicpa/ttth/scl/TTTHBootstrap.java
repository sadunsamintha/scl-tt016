package com.sicpa.ttth.scl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.Bootstrap;
import com.sicpa.standard.sasscl.controller.ProductionParametersEvent;
import com.sicpa.standard.sasscl.custoBuilder.CustoBuilder;
import com.sicpa.standard.sasscl.devices.remote.impl.dtoConverter.DailyBatchRequestRepository;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.statistics.DailyBatchJobStatistics;
import com.sicpa.standard.sasscl.model.statistics.StatisticsKey;
import com.sicpa.standard.sasscl.provider.ProductBatchJobIdProvider;
import com.sicpa.ttth.storage.TTTHFileStorage;

import static com.sicpa.standard.sasscl.messages.ActionMessageType.ERROR;
import static com.sicpa.standard.sasscl.messages.ActionMessageType.WARNING;
import static com.sicpa.ttth.messages.TTTHMessageEventKey.SKUSELECTION.BARCODE_VERIFIED;
import static com.sicpa.ttth.messages.TTTHMessageEventKey.SKUSELECTION.BARCODE_VERIFIED_MSG_CODE;
import static com.sicpa.ttth.messages.TTTHMessageEventKey.SKUSELECTION.DAILY_BATCH_DATED;
import static com.sicpa.ttth.messages.TTTHMessageEventKey.SKUSELECTION.DAILY_BATCH_DATED_MSG_CODE;
import static com.sicpa.ttth.messages.TTTHMessageEventKey.SKUSELECTION.DAILY_BATCH_EXCEEDED;
import static com.sicpa.ttth.messages.TTTHMessageEventKey.SKUSELECTION.DAILY_BATCH_EXCEEDED_MSG_CODE;
import static com.sicpa.ttth.scl.utils.TTTHCalendarUtils.TH_YEAR_DIFF;

public class TTTHBootstrap extends Bootstrap implements ProductBatchJobIdProvider {

	private DailyBatchRequestRepository dailyBatchRequestRepository;

	@Override
	public void executeSpringInitTasks(){
		super.executeSpringInitTasks();
		saveDailyBatchJobStats();
		loadDailyBatchJobStats();
		dailyBatchJobVailidityCheck();
		addSkuSelectionMessages();
		addErrorMessagesForDailyBatchJobs();
	}

	@Override
	protected void restorePreviousSelectedProductionParams() {
		ProductionParameters previous = storage.getSelectedProductionParameters();
		if (productionParametersValidator.validate(previous)) {
			if (previous.getProductionMode().equals(ProductionMode.EXPORT) ||
				previous.getProductionMode().equals(ProductionMode.MAINTENANCE)) {
				//Export or Maintenance Mode
				loadPreviousProductionParams(previous, false);
				restoreStatistics();
				return;
			}
			if (!isBatchJobIdDated(previous.getProperty(productionBatchJobId))) {
				loadPreviousProductionParams(previous, true);
				loadPreviousBatchJobStats();
				restoreStatistics();
			}
		}
	}

	private void loadPreviousProductionParams(ProductionParameters previous, boolean isBatchJobPresent) {
		productionParameters.setBarcode(previous.getBarcode());
		productionParameters.setSku(previous.getSku());
		productionParameters.setProductionMode(previous.getProductionMode());
		if (isBatchJobPresent) {
			productionParameters.setProperty(productionBatchJobId, previous.getProperty(productionBatchJobId));
		}
		EventBusService.post(new ProductionParametersEvent(previous));
	}

	private void loadPreviousBatchJobStats() {
		DailyBatchJobStatistics batchJobStats =
			((TTTHFileStorage)storage).getDailyBatchJobStats(productionParameters.getProperty(productionBatchJobId));
		if (batchJobStats != null) {
			dailyBatchRequestRepository
				.setBatchJobStatistics(batchJobStats);
			dailyBatchRequestRepository
				.setProductsCount(statistics.getValues().get(StatisticsKey.TOTAL));
		}
	}

	private void loadDailyBatchJobStats() {
		CustoBuilder.addActionOnConnectedApplicationState(() -> {
			if (productionParameters.getProductionMode().equals(ProductionMode.STANDARD)) {
				loadPreviousBatchJobStats();
				if (statistics.getValues().get(StatisticsKey.TOTAL) == 0) {
					dailyBatchRequestRepository.setProductsCount(0);
				}
			}
		});
	}

	private void saveDailyBatchJobStats() {
		CustoBuilder.addActionOnStoppingProduction(() -> {
			if (productionParameters.getProductionMode().equals(ProductionMode.STANDARD)) {
				dailyBatchRequestRepository.updateStatistics(statistics.getValues().get(StatisticsKey.TOTAL));
				((TTTHFileStorage)storage).saveDailyBatchJobStats(dailyBatchRequestRepository.getBatchJobStatistics());
			}
		});
	}

	private void dailyBatchJobVailidityCheck() {
		CustoBuilder.addActionOnStartedProduction(() -> {
			if (productionParameters.getProductionMode().equals(ProductionMode.STANDARD)) {
				//In the scenario application wasn't
				// turned off and it passed to the next day.
				if (isBatchJobIdDated(productionParameters
					.getProperty(productionBatchJobId))) {
					EventBusService.post(new MessageEvent(this, DAILY_BATCH_DATED));
				}
				//If production has already exceeded batch job quantity.
				if (dailyBatchRequestRepository
					.getBatchJobStatistics()
					.getProductCount() > dailyBatchRequestRepository
					.getBatchJobStatistics()
					.getBatchQuantity()) {
					EventBusService.post(new MessageEvent(this, DAILY_BATCH_EXCEEDED));
				}
			}
		});
	}

	private void addSkuSelectionMessages() {
		CustoBuilder.addMessage(BARCODE_VERIFIED, BARCODE_VERIFIED_MSG_CODE, WARNING);
	}

	private void addErrorMessagesForDailyBatchJobs() {
		CustoBuilder.addMessage(DAILY_BATCH_DATED, DAILY_BATCH_DATED_MSG_CODE, ERROR);
		CustoBuilder.addMessage(DAILY_BATCH_EXCEEDED, DAILY_BATCH_EXCEEDED_MSG_CODE, ERROR);
	}

	private boolean isBatchJobIdDated(String batchJobId) {
		String[] batchJobArr = batchJobId.split("-");
		Calendar batchCal = Calendar.getInstance();
		Calendar now = Calendar.getInstance();
		now.add(Calendar.YEAR, TH_YEAR_DIFF);
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy");
		try {
			batchCal.setTime(sdf.parse(batchJobArr[2]));
			if (now.get(Calendar.DAY_OF_YEAR) != batchCal.get(Calendar.DAY_OF_YEAR)) {
				//now is after the batch date.
				return true;
			}
		} catch (ParseException e) {
			logger.error("Failed to parse saved batch job date", e);
			return true;
		}
		return false;
	}

	public void setDailyBatchRequestRepository(DailyBatchRequestRepository dailyBatchRequestRepository) {
		this.dailyBatchRequestRepository = dailyBatchRequestRepository;
	}
}
