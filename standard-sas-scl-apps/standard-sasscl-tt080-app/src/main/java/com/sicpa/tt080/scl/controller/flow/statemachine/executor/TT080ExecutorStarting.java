package com.sicpa.tt080.scl.controller.flow.statemachine.executor;

import com.sicpa.standard.client.common.controller.predicate.start.IStartProductionValidator;
import com.sicpa.standard.client.common.controller.predicate.start.NoStartReason;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.client.common.utils.TaskExecutor;
import com.sicpa.standard.sasscl.common.utils.Timeout;
import com.sicpa.standard.sasscl.controller.flow.statemachine.executor.ExecutorStarting;
import com.sicpa.standard.sasscl.controller.hardware.IHardwareController;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimerTask;

/**
 * Overwritting of the ExecutorStarting class to manage
 * the saving of the new BatchId into the selectedProductionParam file
 *
 * @author mjimenez
 *
 */
public class TT080ExecutorStarting extends ExecutorStarting {
	private final static Logger logger = LoggerFactory.getLogger(TT080ExecutorStarting.class);

	private IStartProductionValidator startValidators;
	private int timeoutDelay = 120000;
	private IHardwareController hardwareController;

	// to be able to stop the production if the start takes too long
	private Timeout timeoutStart;

	@Override
	public void enter() {
		TaskExecutor.execute(this::startProduction);
	}

	@Override
	public void startProduction() {
		if (!validatePossibleToStart()) {
			return;
		}

		startTimeoutStart();
		startHardware();
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

	public void setHardwareController(IHardwareController hardwareController) {
		this.hardwareController = hardwareController;
	}

}
