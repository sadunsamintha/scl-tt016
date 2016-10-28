package com.sicpa.standard.sasscl.controller.flow.statemachine.executor;

import com.sicpa.standard.client.common.controller.predicate.start.IStartProductionValidator;
import com.sicpa.standard.client.common.controller.predicate.start.NoStartReason;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.client.common.statemachine.IStateAction;
import com.sicpa.standard.client.common.utils.TaskExecutor;
import com.sicpa.standard.sasscl.business.alert.IAlert;
import com.sicpa.standard.sasscl.common.utils.Timeout;
import com.sicpa.standard.sasscl.controller.hardware.IHardwareController;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.provider.impl.ProductionBatchProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimerTask;

public class ExecutorStarting implements IStateAction {

	private final static Logger logger = LoggerFactory.getLogger(ExecutorStarting.class);

	private IStartProductionValidator startValidators;
	private int timeoutDelay;
	private ProductionBatchProvider productionBatchProvider;
	private IAlert alert;
	private IHardwareController hardwareController;

	// to be able to stop the production if the start takes too long
	private Timeout timeoutStart;

	@Override
	public void enter() {
		TaskExecutor.execute(this::startProduction);
	}

	public void startProduction() {
		if (!validatePossibleToStart()) {
			return;
		}

		startTimeoutStart();
		updateNextProductionBatchTime();
		startHardware();
        alert.start();
        stopTimeoutStart();
	}

	private void startHardware() {
		try {
			hardwareController.start();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			EventBusService.post(new MessageEvent(MessageEventKey.FlowControl.START_FAILED));
		}
	}

	private void updateNextProductionBatchTime() {
		String sTime = System.currentTimeMillis() + "";
		productionBatchProvider.set(sTime);
	}

	private boolean validatePossibleToStart() {
		NoStartReason reasons = startValidators.isPossibleToStartProduction();

        for (String r : reasons.getReasons()) {
			logger.error("failed to start:" + r);
			EventBusService.post(new MessageEvent(r));
		}

		return reasons.isEmpty();
	}

	private void startTimeoutStart() {
		timeoutStart = createStartTimeout();
		timeoutStart.start();
	}

	private void stopTimeoutStart() {
		if (timeoutStart != null) {
			timeoutStart.stop();
		}
	}

	private Timeout createStartTimeout() {
		return new Timeout("Start timeout", new TimerTask() {
			@Override
			public void run() {
				logger.error("Start Timeout reached");
				// start in a new thread to not break the timer by throwing a runtime exception
				EventBusService.post(new MessageEvent(MessageEventKey.FlowControl.START_TIMEOUT));
			}
		}, timeoutDelay);
	}

	public void setStartValidators(IStartProductionValidator startValidators) {
		this.startValidators = startValidators;
	}

	public void setTimeoutDelay(int timeoutDelay) {
		this.timeoutDelay = timeoutDelay;
	}

	public void setProductionBatchProvider(ProductionBatchProvider productionBatchProvider) {
		this.productionBatchProvider = productionBatchProvider;
	}

	public void setAlert(IAlert alert) {
		this.alert = alert;
	}

	public void setHardwareController(IHardwareController hardwareController) {
		this.hardwareController = hardwareController;
	}

	@Override
	public void leave() {
	}
}
