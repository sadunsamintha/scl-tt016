package com.sicpa.standard.sasscl.controller.flow.statemachine;

import com.sicpa.standard.client.common.statemachine.IStateAction;
import com.sicpa.standard.client.common.statemachine.State;
import com.sicpa.standard.client.common.statemachine.Trigger;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;

public interface IStateMachine {

	void moveToNextState(final Trigger t, final String message);

	void moveToPreviousState();

	void addState(State s, IStateAction task);

	ApplicationFlowState getPreviousState();

	ApplicationFlowState getCurrentState();

}
