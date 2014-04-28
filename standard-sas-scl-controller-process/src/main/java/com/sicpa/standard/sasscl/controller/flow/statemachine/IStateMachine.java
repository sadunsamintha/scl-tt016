package com.sicpa.standard.sasscl.controller.flow.statemachine;

import com.sicpa.standard.gui.state.State;
import com.sicpa.standard.gui.state.Trigger;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;

public interface IStateMachine {

	void moveToNextState(final Trigger t, final String message);

	void moveToPreviousState();

	void addState(State s, Runnable task);

	ApplicationFlowState getPreviousState();

	ApplicationFlowState getCurrentState();

}
