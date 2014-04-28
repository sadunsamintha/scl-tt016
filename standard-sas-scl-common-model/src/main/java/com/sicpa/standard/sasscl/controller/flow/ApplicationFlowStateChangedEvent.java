package com.sicpa.standard.sasscl.controller.flow;

public class ApplicationFlowStateChangedEvent {
	protected ApplicationFlowState previousState;
	protected ApplicationFlowState currentState;
	protected String message;

	public ApplicationFlowStateChangedEvent(ApplicationFlowState previousState, ApplicationFlowState currentState,
			String message) {
		this.previousState = previousState;
		this.currentState = currentState;
		this.message = message;
	}

	public ApplicationFlowStateChangedEvent(ApplicationFlowState currentState) {
		this(null, currentState, "");
	}

	public ApplicationFlowState getCurrentState() {
		return currentState;
	}

	public ApplicationFlowState getPreviousState() {
		return previousState;
	}

	public void setMessage(final String message) {
		this.message = message;
	}

	public String getMessage() {
		return this.message;
	}
}
