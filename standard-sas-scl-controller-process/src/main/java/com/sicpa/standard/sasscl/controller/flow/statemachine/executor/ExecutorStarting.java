package com.sicpa.standard.sasscl.controller.flow.statemachine.executor;

import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.controller.predicate.start.IStartProductionValidator;
import com.sicpa.standard.client.common.controller.predicate.start.NoStartReason;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.client.common.statemachine.IStateAction;
import com.sicpa.standard.client.common.utils.TaskExecutor;
import com.sicpa.standard.sasscl.business.alert.IAlert;
import com.sicpa.standard.sasscl.business.statistics.IStatistics;
import com.sicpa.standard.sasscl.common.utils.Timeout;
import com.sicpa.standard.sasscl.controller.hardware.IHardwareController;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.provider.impl.ProductionBatchProvider;

public class ExecutorStarting implements IStateAction {

	private final static Logger logger = LoggerFactory.getLogger(ExecutorStarting.class);

	// to be able to stop the production if the start takes too long
	protected Timeout timeoutStart;

	protected IStatistics statistics;
	protected IStartProductionValidator startValidators;

	protected int timeoutDelay = 30000;
	protected ProductionBatchProvider productionBatchProvider;
	protected IAlert alert;

	protected IHardwareController hardwareController;

	@Override
	public void enter() {
		TaskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				startProduction();
			}
		});
	}

	public void startProduction() {

		NoStartReason reasons = startValidators.isPossibleToStartProduction();
		if (!reasons.isEmpty()) {
			for (String r : reasons.getReasons()) {
				logger.error("failed to start:" + r);
				EventBusService.post(new MessageEvent(r));
			}
			return;
		}

		startTimeoutStart();

		String stime = System.currentTimeMillis() + "";
		productionBatchProvider.set(stime);
		alert.start();

		try {
			hardwareController.start();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			EventBusService.post(new MessageEvent(MessageEventKey.FlowControl.START_FAILED));
		}
		stopTimeoutStart();
	}

	public void setStatistics(IStatistics statistics) {
		this.statistics = statistics;
	}

	protected void startTimeoutStart() {
		timeoutStart = createStartTimeout();
		timeoutStart.start();
	}

	protected void stopTimeoutStart() {
		if (timeoutStart != null) {
			timeoutStart.stop();
		}
	}

	protected Timeout createStartTimeout() {
		return new Timeout("Start timeout", new TimerTask() {
			@Override
			public void run() {
				logger.error("Start Timeout reached");
				// start in a new thread to not break the timer by throwing a
				// runtime exception
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
