package com.sicpa.standard.sasscl.controller.flow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.statemachine.State;
import com.sicpa.standard.client.common.statemachine.Trigger;

public class ApplicationFlowState extends State {

	public static final ApplicationFlowState STT_NO_SELECTION = new ApplicationFlowState("STT_NO_SELECTION");
	public static final ApplicationFlowState STT_CONNECTED = new ApplicationFlowState("STT_CONNECTED");
	public static final ApplicationFlowState STT_CONNECTING = new ApplicationFlowState("STT_CONNECTING");
	public static final ApplicationFlowState STT_EXIT = new ApplicationFlowState("STT_EXIT");
	public static final ApplicationFlowState STT_SELECT_NO_PREVIOUS = new ApplicationFlowState("STT_SELECT_NO_PREVIOUS");
	public static final ApplicationFlowState STT_SELECT_WITH_PREVIOUS = new ApplicationFlowState("STT_SELECT_WITH_PREVIOUS");
	public static final ApplicationFlowState STT_STARTED = new ApplicationFlowState("STT_STARTED");
	public static final ApplicationFlowState STT_STARTING = new ApplicationFlowState("STT_STARTING");
	public static final ApplicationFlowState STT_STOPPING = new ApplicationFlowState("STT_STOPPING");
	public static final ApplicationFlowState STT_RECOVERING = new ApplicationFlowState("STT_RECOVERING");
	public static final ApplicationFlowState STT_DISCONNECTING_ON_PARAM_CHANGED = new ApplicationFlowState("STT_DISCONNECTING_ON_PARAM_CHANGED");

	private static Logger logger = LoggerFactory.getLogger(ApplicationFlowState.class);

	public ApplicationFlowState(final String name) {
		super(name);
	}

	public void reset() {
		this.nextStates.clear();
	}

	@Override
	protected void noNextStateFound(Trigger t) {
		logger.error("\nnext state not found. current state={} , trigger={}", this, t);
	}

	/**
	 * shortcut for addNextPossibleState
	 */
	public void addNext(Trigger t, State s) {
		addNextPossibleState(t, s);
	}
}
