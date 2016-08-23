package com.sicpa.standard.sasscl.view.startstop;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.sasscl.common.log.OperatorLogger;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.controller.flow.IFlowControl;

import java.util.List;

import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_CONNECTED;
import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_STARTED;
import static java.util.Arrays.asList;


public class StartStopViewController implements IStartStopViewListener {

	private final List<ApplicationFlowState> enableStartState = asList(STT_CONNECTED);
	private final List<ApplicationFlowState> enableStopState = asList(STT_STARTED);

	private StartStopModel model;
	private IFlowControl flowControl;

	public StartStopViewController(StartStopModel model) {
		this.model = model;
	}

	public StartStopViewController() {
		this(new StartStopModel());
	}

	@Subscribe
	public void processStateChanged(final ApplicationFlowStateChangedEvent evt) {
		model.setStartEnabled(enableStartState.contains(evt.getCurrentState()));
		model.setStopEnabled(enableStopState.contains(evt.getCurrentState()));
		model.notifyModelChanged();
	}

	@Override
	public void start() {
		OperatorLogger.log("Start Production");
		flowControl.notifyStartProduction();
	}

	@Override
	public void stop() {
		OperatorLogger.log("Stop Production");
		flowControl.notifyStopProduction();
	}

	public StartStopModel getModel() {
		return model;
	}

	public void setFlowControl(IFlowControl flowControl) {
		this.flowControl = flowControl;
	}
}
