package com.sicpa.standard.sasscl.controller.flow.statemachine.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.statemachine.IStateAction;
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

public class ExecutorExit implements IStateAction {

	private static final Logger logger = LoggerFactory.getLogger(ExecutorExit.class);

	private IHardwareController hardwareController;
	private IProduction production;
	private IStorage storage;
	private IStatistics statistics;
	private ScreensFlow screensFlow;
	private boolean switchOffPrinterOnExit;

	public ExecutorExit() {
		Runtime.getRuntime().addShutdownHook(new Thread(this::executeShutdownHook));
	}

	@Override
	public void enter() {
		TaskExecutor.execute(() -> {
            MonitoringService.addSystemEvent(new BasicSystemEvent(SystemEventLevel.INFO,
                    SystemEventType.APP_EXITING));
            logger.info("Application is exiting");

            screensFlow.moveToNext(ScreensFlowTriggers.EXIT);

            switchOffAndDisconnect();

            storage.saveStatistics(statistics.getValues());
            production.saveProductionData();
            production.packageProduction();

            // unlock the screen to be able to exit during the production sending
            EventBusService.post(new UnlockFullScreenEvent());
            production.onExitSendAllProductionData();

            SingleAppInstanceUtils.releaseLock();

            exitJVM();
        });
	}

	protected void executeShutdownHook() {
		logger.info("Executing shutdown hook");
		switchOffAndDisconnect();
		production.saveProductionData();
		storage.saveStatistics(statistics.getValues());
	}

	private void switchOffAndDisconnect() {
		try {
			if (switchOffPrinterOnExit) {
				hardwareController.switchOff();
			}
			hardwareController.disconnect();
		} catch (Exception e) {
			logger.error("Error switching off printer and disconnecting devices", e);
		}
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

	@Override
	public void leave() {
	}

	public void setSwitchOffPrinterOnExit(boolean switchOffPrinterOnExit) {
		this.switchOffPrinterOnExit = switchOffPrinterOnExit;
	}
}
