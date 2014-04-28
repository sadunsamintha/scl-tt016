package com.sicpa.standard.sasscl.controller.flow.statemachine;
import java.util.Map;

import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;

/**
 * 
 * represent the application state machine internal wiring
 * 
 * @author DIelsch
 * 
 */
public interface IFlowControlWiring {

	/**
	 * 
	 * @return map<state,executor>
	 */
	Map<ApplicationFlowState, Runnable> getStateMap();

	/**
	 * 
	 * @return first state of the application
	 */
	ApplicationFlowState getInitialState();

}
