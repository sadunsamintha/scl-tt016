package com.sicpa.ttth.scl;

import com.google.common.eventbus.Subscribe;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.Bootstrap;
import com.sicpa.standard.sasscl.controller.ProductionParametersEvent;
import com.sicpa.standard.sasscl.custoBuilder.CustoBuilder;
import com.sicpa.standard.sasscl.devices.plc.EjectorPlcEnums;
import com.sicpa.standard.sasscl.devices.remote.AbstractRemoteServer;
import com.sicpa.standard.sasscl.devices.remote.impl.dtoConverter.DailyBatchRequestRepository;
import com.sicpa.standard.sasscl.event.DailyBatchJobStatsDeleteEvt;
import com.sicpa.standard.sasscl.model.BatchJobHistory;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.statistics.DailyBatchJobStatistics;
import com.sicpa.standard.sasscl.model.statistics.StatisticsKey;
import com.sicpa.standard.sasscl.provider.ProductBatchJobIdProvider;
import com.sicpa.ttth.remote.server.TTTHRemoteServer;
import com.sicpa.ttth.scl.utils.TTTHDailyBatchJobUtils;
import com.sicpa.ttth.storage.TTTHFileStorage;

import static com.sicpa.standard.sasscl.messages.ActionMessageType.ERROR;
import static com.sicpa.standard.sasscl.messages.ActionMessageType.WARNING;
import static com.sicpa.ttth.messages.TTTHMessageEventKey.EJECTOR.PLC_EJECTOR_CAPACITY_ERROR;
import static com.sicpa.ttth.messages.TTTHMessageEventKey.EJECTOR.PLC_EJECTOR_CAPACITY_ERROR_MSG_CODE;
import static com.sicpa.ttth.messages.TTTHMessageEventKey.EJECTOR.PLC_EJECTOR_CAPACITY_WARNING;
import static com.sicpa.ttth.messages.TTTHMessageEventKey.EJECTOR.PLC_EJECTOR_CAPACITY_WARNING_MSG_CODE;
import static com.sicpa.ttth.messages.TTTHMessageEventKey.EJECTOR.PLC_EJECTOR_CONFIRMATION_ERROR;
import static com.sicpa.ttth.messages.TTTHMessageEventKey.EJECTOR.PLC_EJECTOR_CONFIRMATION_ERROR_MSG_CODE;
import static com.sicpa.ttth.messages.TTTHMessageEventKey.SKUSELECTION.BARCODE_VERIFIED;
import static com.sicpa.ttth.messages.TTTHMessageEventKey.SKUSELECTION.BARCODE_VERIFIED_MSG_CODE;
import static com.sicpa.ttth.messages.TTTHMessageEventKey.SKUSELECTION.DAILY_BATCH_DATED;
import static com.sicpa.ttth.messages.TTTHMessageEventKey.SKUSELECTION.DAILY_BATCH_DATED_MSG_CODE;
import static com.sicpa.ttth.messages.TTTHMessageEventKey.SKUSELECTION.DAILY_BATCH_EXCEEDED;
import static com.sicpa.ttth.messages.TTTHMessageEventKey.SKUSELECTION.DAILY_BATCH_EXCEEDED_MSG_CODE;

public class TTTHBootstrap extends Bootstrap implements ProductBatchJobIdProvider {

	private DailyBatchRequestRepository dailyBatchRequestRepository;
	private AbstractRemoteServer remoteServer;

	private int getCodedCountInterval;

	@Override
	public void executeSpringInitTasks(){
		addMessagesForEjector();
		loadDailyBatchJobStats();
		loadPreviousBatchJobHistory();
		super.executeSpringInitTasks();
		dailyBatchJobValidityCheck();
		addSkuSelectionMessages();
		addErrorMessagesForDailyBatchJobs();
		getCodedCount();
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
			} else {
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

	private void loadPreviousBatchJobHistory() {
		BatchJobHistory batchJobHistory =
			((TTTHFileStorage)storage).getBatchJobHistory();
		if (batchJobHistory != null) {
			dailyBatchRequestRepository
				.setBatchJobHistory(batchJobHistory);
			dailyBatchRequestRepository
				.getBatchJobHistory().clearOldBatchJobs();
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

	private synchronized void getCodedCount() {
		ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
		executorService.scheduleAtFixedRate(() -> {
			if (productionParameters.getProductionMode().equals(ProductionMode.STANDARD)) {
				if (remoteServer instanceof TTTHRemoteServer) {
					Integer value = ((TTTHRemoteServer)remoteServer)
						.getActualCodedCount(dailyBatchRequestRepository.getBatchJobStatistics().getBatchJobId());
					if (value != null) {
						dailyBatchRequestRepository.getBatchJobStatistics().setProductCount(value);
						saveDailyBatchJobStats();
					}
				} else {
					//Simulator
					dailyBatchRequestRepository
						.getBatchJobStatistics()
						.setProductCount(statistics.getValues().get(StatisticsKey.TOTAL));
					saveDailyBatchJobStats();
				}
			}
		}, getCodedCountInterval, getCodedCountInterval, TimeUnit.SECONDS);
	}

	private void saveDailyBatchJobStats() {
		((TTTHFileStorage)storage).saveDailyBatchJobStats(dailyBatchRequestRepository.getBatchJobStatistics());
		((TTTHFileStorage)storage).saveBatchJobHistory(dailyBatchRequestRepository.getBatchJobHistory());
		dailyBatchRequestRepository.getBatchJobHistory().clearOldBatchJobs();
	}

	@Subscribe
	public void deleteOldDailyBatchJobStats(DailyBatchJobStatsDeleteEvt evt) {
		for (String dailyBatchJob : evt.getDailyBatchJobs()) {
			((TTTHFileStorage)storage).deleteDailyBatchJobStats(dailyBatchJob);
		}
	}

	private void dailyBatchJobValidityCheck() {
		CustoBuilder.addActionOnStartedProduction(() -> {
			if (productionParameters.getProductionMode().equals(ProductionMode.STANDARD)) {
				if (TTTHDailyBatchJobUtils.isBatchJobIdDated(dailyBatchRequestRepository.
					getBatchJobStatistics().getBatchJobStopDate())) {
					EventBusService.post(new MessageEvent(this, DAILY_BATCH_DATED));
				}
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

	public static void addEjectorPlcVariables() {
		for (EjectorPlcEnums var : EjectorPlcEnums.values()) {
			CustoBuilder.addPlcVariable(var.toString(), var.getNameOnPlc(), var.getPlc_type(), new HashMap<String, String>() {{
				put("lineGrp", "ejection");
			}});
		}
	}

	private void addMessagesForEjector() {
		CustoBuilder.addMessage(PLC_EJECTOR_CAPACITY_WARNING, PLC_EJECTOR_CAPACITY_WARNING_MSG_CODE, WARNING);
		CustoBuilder.addMessage(PLC_EJECTOR_CAPACITY_ERROR, PLC_EJECTOR_CAPACITY_ERROR_MSG_CODE, ERROR);
		CustoBuilder.addMessage(PLC_EJECTOR_CONFIRMATION_ERROR, PLC_EJECTOR_CONFIRMATION_ERROR_MSG_CODE, ERROR);
	}

	public void setDailyBatchRequestRepository(DailyBatchRequestRepository dailyBatchRequestRepository) {
		this.dailyBatchRequestRepository = dailyBatchRequestRepository;
	}

	public void setRemoteServer(AbstractRemoteServer remoteServer) {
		this.remoteServer = remoteServer;
	}

	public void setGetCodedCountInterval(int getCodedCountInterval) {
		this.getCodedCountInterval = getCodedCountInterval;
	}
}
