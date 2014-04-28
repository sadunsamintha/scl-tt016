package com.sicpa.standard.sasscl.controller.flow.statemachine;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;

public abstract class AbstractFlowControlWiring implements IFlowControlWiring {

	protected Map<ApplicationFlowState, Runnable> map = new HashMap<ApplicationFlowState, Runnable>();
	protected List<FlowTransition> flowTransitions = new ArrayList<FlowTransition>();

	public AbstractFlowControlWiring() {
		initFlowTransitions();
	}

	public void addNext(ApplicationFlowState current, FlowTransition... flowTransitions) {
		for (FlowTransition t : flowTransitions) {
			current.addNext(t.getTrigger(), t.getNextState());
		}
	}

	@Override
	public Map<ApplicationFlowState, Runnable> getStateMap() {
		if (map.isEmpty()) {
			map.putAll(createStateMap());
		}
		return map;
	}

	protected abstract Map<ApplicationFlowState, Runnable> createStateMap();

	protected abstract void initFlowTransitions();

}
