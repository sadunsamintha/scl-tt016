package com.sicpa.standard.sasscl.controller.flow.statemachine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sicpa.standard.client.common.statemachine.IStateAction;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;

public abstract class AbstractFlowControlWiring implements IFlowControlWiring {

	protected Map<ApplicationFlowState, IStateAction> map = new HashMap<>();
	protected List<FlowTransition> flowTransitions = new ArrayList<>();

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
