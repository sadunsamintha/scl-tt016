package com.sicpa.standard.sasscl.controller.flow;

import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.statemachine.IStateAction;
import com.sicpa.standard.client.common.utils.TaskExecutor;
import com.sicpa.standard.sasscl.controller.flow.statemachine.IFlowControlWiring;
import com.sicpa.standard.sasscl.controller.flow.statemachine.IStateMachine;

public abstract class AbstractFlowControl implements IFlowControl {

	private static Logger logger = LoggerFactory.getLogger(AbstractFlowControl.class);

	protected IStateMachine stateMachine;

	protected IFlowControlWiring flowControlWiring;

	public AbstractFlowControl() {

	}

	/**
	 * called by spring
	 */
	public void initMachine() {
		for (Entry<ApplicationFlowState, IStateAction> entry : flowControlWiring.getStateMap().entrySet()) {
			stateMachine.addState(entry.getKey(), entry.getValue());
		}
	}

	public void setStateMachine(IStateMachine stateMachine) {
		this.stateMachine = stateMachine;
	}

	public void setFlowControlWiring(IFlowControlWiring flowControlWiring) {
		this.flowControlWiring = flowControlWiring;
	}

	protected final Object lock = new Object();

	protected void moveToNextState(final ActivityTrigger t) {
		TaskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				synchronized (lock) {
					try {
						stateMachine.moveToNextState(t, "");
					} catch (Exception e) {
						if (e instanceof RuntimeException) {
							throw (RuntimeException) e;
						} else {
							logger.error("", e);
						}
					}
				}
			}
		});
	}

}
