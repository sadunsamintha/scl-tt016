package com.sicpa.standard.sasscl.controller.flow.statemachine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.gui.state.State;
import com.sicpa.standard.gui.state.StateMachine;
import com.sicpa.standard.gui.state.Trigger;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.monitoring.MonitoringService;
import com.sicpa.standard.sasscl.monitoring.system.SystemEventLevel;
import com.sicpa.standard.sasscl.monitoring.system.SystemEventType;
import com.sicpa.standard.sasscl.monitoring.system.event.BasicSystemEvent;

public class FlowControlStateMachine extends StateMachine implements IStateMachine {

	private static final Logger logger = LoggerFactory.getLogger(FlowControlStateMachine.class);

	public FlowControlStateMachine(final State initState) {
		super(initState);
		fireActivityChanged(getCurrentState(), getPreviousState());
	}

	protected Object lock = new Object();

	protected String previousMessage;

	public void moveToNextState(final Trigger t, final String message) {
		synchronized (lock) {
			ApplicationFlowState previous = (ApplicationFlowState) getCurrentState();
			super.moveToNextState(t);
			ApplicationFlowState newState = (ApplicationFlowState) getCurrentState();
			logger.info("Application state changed:{}+{}=>{}", new Object[] { previous, t, newState });
			if (!previous.equals(newState)) {
				MonitoringService.addSystemEvent(new BasicSystemEvent(SystemEventLevel.INFO,
						SystemEventType.STATE_CHANGED, newState + ""));
				fireActivityChanged(newState, previous, message);
			} else if (previousMessage != null && !previousMessage.equals(message)) {
				// same state but message changed
				fireActivityChanged(newState, previous, message);
			}
			previousMessage = message;
		}
	}

	@Override
	public void moveToPreviousState() {
		// not impl
	}

	protected void fireActivityChanged(final ApplicationFlowState state, final ApplicationFlowState previousState) {
		fireActivityChanged(state, previousState, "");
	}

	protected void fireActivityChanged(ApplicationFlowState currentState, ApplicationFlowState previousState,
			String message) {
		EventBusService.post(new ApplicationFlowStateChangedEvent(previousState, currentState, message));
	}

	@Override
	public ApplicationFlowState getPreviousState() {
		return (ApplicationFlowState) super.getPreviousState();
	}

	@Override
	public ApplicationFlowState getCurrentState() {
		return (ApplicationFlowState) super.getCurrentState();
	}

}