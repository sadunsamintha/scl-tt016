package com.sicpa.standard.sasscl.controller.flow.statemachine;
import com.sicpa.standard.client.common.statemachine.Trigger;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;

public class FlowTransition {
	Trigger trigger;
	ApplicationFlowState nextState;

	public FlowTransition(Trigger trigger, ApplicationFlowState nextState) {
		this.trigger = trigger;
		this.nextState = nextState;
	}

	public Trigger getTrigger() {
		return trigger;
	}

	public ApplicationFlowState getNextState() {
		return nextState;
	}
}