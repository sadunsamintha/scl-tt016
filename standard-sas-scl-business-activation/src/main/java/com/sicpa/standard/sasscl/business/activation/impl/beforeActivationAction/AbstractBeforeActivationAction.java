package com.sicpa.standard.sasscl.business.activation.impl.beforeActivationAction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.sasscl.model.Code;

public abstract class AbstractBeforeActivationAction implements IBeforeActivationAction {

	private static Logger logger = LoggerFactory.getLogger(AbstractBeforeActivationAction.class);

	protected IBeforeActivationAction nextAction;

	protected abstract BeforeActivationResult internalReceivedCode(Code code, boolean good, String cameraName);

	@Override
	public BeforeActivationResult receiveCode(final Code code, final boolean good, String cameraName) {
		BeforeActivationResult res = internalReceivedCode(code, good, cameraName);
		if (this.nextAction != null && !res.isFiltered()) {
			res = this.nextAction.receiveCode(res.getCode(), res.isValid(), cameraName);
		}
		return res;
	}

	public void setNextAction(final IBeforeActivationAction nextAction) {
		this.nextAction = nextAction;
	}
}