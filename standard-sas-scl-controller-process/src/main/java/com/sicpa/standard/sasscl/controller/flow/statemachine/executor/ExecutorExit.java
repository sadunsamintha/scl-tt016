package com.sicpa.standard.sasscl.controller.flow.statemachine.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.utils.SingleAppInstanceUtils;
import com.sicpa.standard.client.common.utils.TaskExecutor;
import com.sicpa.standard.client.common.view.screensflow.ScreensFlow;
import com.sicpa.standard.sasscl.business.production.IProduction;
import com.sicpa.standard.sasscl.business.statistics.IStatistics;
import com.sicpa.standard.sasscl.common.storage.IStorage;
import com.sicpa.standard.sasscl.controller.hardware.IHardwareController;
import com.sicpa.standard.sasscl.event.UnlockFullScreenEvent;
import com.sicpa.standard.sasscl.monitoring.MonitoringService;
import com.sicpa.standard.sasscl.monitoring.system.SystemEventLevel;
import com.sicpa.standard.sasscl.monitoring.system.SystemEventType;
import com.sicpa.standard.sasscl.monitoring.system.event.BasicSystemEvent;
import com.sicpa.standard.sasscl.view.ScreensFlowTriggers;

public class ExecutorExit implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(ExecutorExit.class);

	protected IHardwareController hardwareController;
	protected IProduction production;
	protected IStorage storage;
	protected IStatistics statistics;
	protected ScreensFlow screensFlow;

	public ExecutorExit() {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				executeShutdownHook();
			}
		}));
	}

	@Override
	public void run() {
		TaskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				MonitoringService.addSystemEvent(new BasicSystemEvent(SystemEventLevel.INFO,
						SystemEventType.APP_EXITING));
				logger.info("Application is exiting");

				screensFlow.moveToNext(ScreensFlowTriggers.EXIT);

				hardwareController.disconnect();

				storage.saveStatistics(statistics.getValues());
				production.saveProductionData();
				production.packageProduction();

				// unlock the screen to be able to exit during the production sending
				EventBusService.post(new UnlockFullScreenEvent());
				production.onExitSendAllProductionData();

				SingleAppInstanceUtils.releaseLock();

				exitJVM();
			}
		});
	}

	protected void executeShutdownHook() {
		logger.info("executing shutdownhook");
		try {
			hardwareController.switchOff();
			hardwareController.disconnect();
		} catch (Exception e) {
		}
		production.saveProductionData();
		
		storage.saveStatistics(statistics.getValues());
	}

	protected void exitJVM() {
		System.exit(0);
	}

	public void setHardwareController(IHardwareController hardwareController) {
		this.hardwareController = hardwareController;
	}

	public void setProduction(IProduction production) {
		this.production = production;
	}

	public void setStorage(IStorage storage) {
		this.storage = storage;
	}

	public void setStatistics(IStatistics statistics) {
		this.statistics = statistics;
	}

	public void setScreensFlow(ScreensFlow screensFlow) {
		this.screensFlow = screensFlow;
	}
}
