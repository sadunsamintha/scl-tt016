package com.sicpa.tt016.business.activation;


import com.sicpa.standard.sasscl.business.activation.impl.beforeActivationAction.AbstractBeforeActivationAction;
import com.sicpa.standard.sasscl.business.activation.impl.beforeActivationAction.BeforeActivationResult;
import com.sicpa.standard.sasscl.model.Code;

import static com.sicpa.standard.sasscl.business.activation.impl.beforeActivationAction.BeforeActivationResult.createBeforeActivationResultNotFiltered;

public class NoActionBeforeActivation extends AbstractBeforeActivationAction {
	@Override
	protected BeforeActivationResult internalReceivedCode(Code code, boolean good, String cameraName) {
		return createBeforeActivationResultNotFiltered(code, good);
	}
}
