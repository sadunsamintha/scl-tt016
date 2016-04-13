package com.sicpa.standard.sasscl.controller.flow.statemachine;

import com.sicpa.standard.client.common.statemachine.IStateAction;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractFlowControlWiring implements IFlowControlWiring {

	protected Map<ApplicationFlowState, IStateAction> map = new HashMap<>();

	public AbstractFlowControlWiring() {
		initFlowTransitions();
	}

	public void addNext(ApplicationFlowState current, FlowTransition... flowTransitions) {
		for (FlowTransition t : flowTransitions) {
			current.addNext(t.getTrigger(), t.getNextState());

		}
	}

	@Override
	public Map<ApplicationFlowState, IStateAction> getStateMap() {
		if (map.isEmpty()) {
			map.putAll(createStateMap());
		}
		return map;
	}

	protected abstract Map<ApplicationFlowState, IStateAction> createStateMap();

	protected abstract void initFlowTransitions();

}
