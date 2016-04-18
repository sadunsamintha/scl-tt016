package com.sicpa.standard.sasscl.business.activation.impl.beforeActivationAction;

import com.sicpa.standard.sasscl.model.Code;

public abstract class AbstractBeforeActivationAction implements IBeforeActivationAction {

	private IBeforeActivationAction nextAction;

	protected abstract BeforeActivationResult internalReceivedCode(Code code, boolean good, String cameraName);

	@Override
	public BeforeActivationResult receiveCode(Code code, boolean good, String cameraName) {
		BeforeActivationResult res = internalReceivedCode(code, good, cameraName);
		if (nextAction != null && !res.isFiltered()) {
			res = nextAction.receiveCode(res.getCode(), res.isValid(), cameraName);
		}
		return res;
	}

	public void setNextAction(final IBeforeActivationAction nextAction) {
		this.nextAction = nextAction;
	}
}