package com.sicpa.standard.sasscl.view.startstop;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.sasscl.common.log.OperatorLogger;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.controller.flow.IFlowControl;

public class StartStopViewController implements IStartStopViewListener {

	protected final List<ApplicationFlowState> enableStartState = new ArrayList<ApplicationFlowState>(
			asList(ApplicationFlowState.STT_CONNECTED));
	protected final List<ApplicationFlowState> enableStopState = new ArrayList<ApplicationFlowState>(
			asList(ApplicationFlowState.STT_STARTED));

	protected StartStopModel model;
	protected IFlowControl flowControl;

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
