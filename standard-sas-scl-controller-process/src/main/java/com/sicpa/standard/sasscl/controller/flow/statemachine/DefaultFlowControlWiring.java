package com.sicpa.standard.sasscl.controller.flow.statemachine;

import static com.sicpa.standard.sasscl.controller.flow.ActivityTrigger.TRG_ENTERSELECTION;
import static com.sicpa.standard.sasscl.controller.flow.ActivityTrigger.TRG_EXITSELECTION;
import static com.sicpa.standard.sasscl.controller.flow.ActivityTrigger.TRG_EXIT_APPLICATION;
import static com.sicpa.standard.sasscl.controller.flow.ActivityTrigger.TRG_HARDWARE_CONNECTED;
import static com.sicpa.standard.sasscl.controller.flow.ActivityTrigger.TRG_HARDWARE_DISCONNECTED;
import static com.sicpa.standard.sasscl.controller.flow.ActivityTrigger.TRG_HARDWARE_STOPPING;
import static com.sicpa.standard.sasscl.controller.flow.ActivityTrigger.TRG_RECOVERING_CONNECTION;
import static com.sicpa.standard.sasscl.controller.flow.ActivityTrigger.TRG_SELECT;
import static com.sicpa.standard.sasscl.controller.flow.ActivityTrigger.TRG_STARTED;
import static com.sicpa.standard.sasscl.controller.flow.ActivityTrigger.TRG_START_PRODUCTION;
import static com.sicpa.standard.sasscl.controller.flow.ActivityTrigger.TRG_STOP_PRODUCTION;
import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_CONNECTED;
import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_CONNECTING;
import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_DISCONNECTING_ON_PARAM_CHANGED;
import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_EXIT;
import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_NO_SELECTION;
import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_RECOVERING;
import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_SELECT_NO_PREVIOUS;
import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_SELECT_WITH_PREVIOUS;
import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_STARTED;
import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_STARTING;
import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_STOPPING;

import java.util.HashMap;
import java.util.Map;

import com.sicpa.standard.client.common.statemachine.IStateAction;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;

public class DefaultFlowControlWiring extends AbstractFlowControlWiring {

	protected IStateAction executorStarted;
	protected IStateAction executorStarting;
	protected IStateAction executorExit;
	protected IStateAction executorNoSelection;
	protected IStateAction executorConnecting;
	protected IStateAction executorSelectWithPrevious;
	protected IStateAction executorConnected;
	protected IStateAction executorSelectNoPrevious;
	protected IStateAction executorStopping;
	protected IStateAction executorDisconnectingOnParamChanged;
	protected IStateAction executorRecovering;

	@Override
	public ApplicationFlowState getInitialState() {
		return STT_NO_SELECTION;
	}

	@Override
	protected Map<ApplicationFlowState, IStateAction> createStateMap() {
		Map<ApplicationFlowState, IStateAction> map = new HashMap<>();
		map.put(STT_NO_SELECTION, executorNoSelection);
		map.put(STT_CONNECTED, executorConnected);
		map.put(STT_CONNECTING, executorConnecting);
		map.put(STT_DISCONNECTING_ON_PARAM_CHANGED, executorDisconnectingOnParamChanged);
		map.put(STT_EXIT, executorExit);
		map.put(STT_SELECT_NO_PREVIOUS, executorSelectNoPrevious);
		map.put(STT_SELECT_WITH_PREVIOUS, executorSelectWithPrevious);
		map.put(STT_STARTED, executorStarted);
		map.put(STT_STARTING, executorStarting);
		map.put(STT_STOPPING, executorStopping);
		map.put(STT_RECOVERING, executorRecovering);
		return map;
	}

	@Override
	protected void initFlowTransitions() {
		//@formatter:off
		addNext(STT_NO_SELECTION,
				new FlowTransition(TRG_ENTERSELECTION, STT_SELECT_NO_PREVIOUS),
				new FlowTransition(TRG_SELECT, STT_CONNECTING),
				new FlowTransition(TRG_EXIT_APPLICATION, STT_EXIT));

		addNext(STT_SELECT_NO_PREVIOUS,
				new FlowTransition(TRG_EXITSELECTION, STT_NO_SELECTION),
				new FlowTransition(TRG_SELECT, STT_CONNECTING),
				new FlowTransition(TRG_EXIT_APPLICATION, STT_EXIT));

		addNext(STT_CONNECTING, 
				new FlowTransition(TRG_HARDWARE_CONNECTED, STT_CONNECTED),
				new FlowTransition(TRG_RECOVERING_CONNECTION, STT_RECOVERING),
				new FlowTransition(TRG_ENTERSELECTION,STT_SELECT_WITH_PREVIOUS),
				new FlowTransition(TRG_EXIT_APPLICATION, STT_EXIT));

		addNext(STT_CONNECTED,
				new FlowTransition(TRG_ENTERSELECTION, STT_SELECT_WITH_PREVIOUS),
				new FlowTransition(TRG_RECOVERING_CONNECTION, STT_RECOVERING),
				new FlowTransition(TRG_START_PRODUCTION, STT_STARTING),
				new FlowTransition(TRG_EXIT_APPLICATION, STT_EXIT));

		addNext(STT_SELECT_WITH_PREVIOUS,
				new FlowTransition(TRG_EXITSELECTION, STT_CONNECTED),
				new FlowTransition(TRG_SELECT, STT_DISCONNECTING_ON_PARAM_CHANGED),
				new FlowTransition(TRG_EXIT_APPLICATION, STT_EXIT));

		addNext(STT_DISCONNECTING_ON_PARAM_CHANGED,
				new FlowTransition(TRG_HARDWARE_DISCONNECTED, STT_CONNECTING),
				new FlowTransition(TRG_EXIT_APPLICATION, STT_EXIT));

		addNext(STT_STARTING,
				new FlowTransition(TRG_STARTED, STT_STARTED),
				new FlowTransition(TRG_RECOVERING_CONNECTION, STT_RECOVERING),
				new FlowTransition(TRG_STOP_PRODUCTION, STT_CONNECTED),
				new FlowTransition(TRG_HARDWARE_STOPPING, STT_STOPPING),
				new FlowTransition(TRG_EXIT_APPLICATION,STT_EXIT));

		addNext(STT_STARTED,
				new FlowTransition(TRG_HARDWARE_STOPPING, STT_STOPPING),
				new FlowTransition(TRG_STOP_PRODUCTION, STT_STOPPING),
				new FlowTransition(TRG_EXIT_APPLICATION, STT_EXIT));

		addNext(STT_STOPPING,
				new FlowTransition(TRG_ENTERSELECTION, STT_SELECT_WITH_PREVIOUS),
				new FlowTransition(TRG_HARDWARE_CONNECTED, STT_CONNECTED),
				new FlowTransition(TRG_RECOVERING_CONNECTION, STT_RECOVERING),
				new FlowTransition(TRG_EXIT_APPLICATION, STT_EXIT));

		addNext(STT_RECOVERING,
				new FlowTransition(TRG_ENTERSELECTION, STT_SELECT_WITH_PREVIOUS),
				new FlowTransition(TRG_HARDWARE_CONNECTED, STT_CONNECTED),
				new FlowTransition(TRG_RECOVERING_CONNECTION, STT_RECOVERING),
				new FlowTransition(TRG_EXIT_APPLICATION, STT_EXIT));
	}
	//@formatter:on
	
	public void setExecutorStarted(IStateAction executorStarted) {
		this.executorStarted = executorStarted;
	}

	public void setExecutorStarting(IStateAction executorStarting) {
		this.executorStarting = executorStarting;
	}

	public void setExecutorNoSelection(IStateAction executorNoSelection) {
		this.executorNoSelection = executorNoSelection;
	}

	public void setExecutorConnecting(IStateAction executorConnecting) {
		this.executorConnecting = executorConnecting;
	}

	public void setExecutorSelectWithPrevious(IStateAction executorSelectWithPrevious) {
		this.executorSelectWithPrevious = executorSelectWithPrevious;
	}

	public void setExecutorConnected(IStateAction executorConnected) {
		this.executorConnected = executorConnected;
	}

	public void setExecutorSelectNoPrevious(IStateAction executorSelectNoPrevious) {
		this.executorSelectNoPrevious = executorSelectNoPrevious;
	}

	public void setExecutorStopping(IStateAction executorStopping) {
		this.executorStopping = executorStopping;
	}

	public void setExecutorDisconnectingOnParamChanged(IStateAction executorDisconnectingOnParamChanged) {
		this.executorDisconnectingOnParamChanged = executorDisconnectingOnParamChanged;
	}

	public void setExecutorExit(IStateAction executorExit) {
		this.executorExit = executorExit;
	}

	public void setExecutorRecovering(IStateAction executorRecovering) {
		this.executorRecovering = executorRecovering;
	}
}
