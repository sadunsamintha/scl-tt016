package com.sicpa.standard.sasscl.business.activation.offline.impl;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.sasscl.business.activation.offline.IOfflineCounting;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;

public class PlcOfflineCountingTrigger {

	private IOfflineCounting offlineCounting;

	public PlcOfflineCountingTrigger(IOfflineCounting offlineCounting) {
		this.offlineCounting = offlineCounting;
	}

	@Subscribe
	public void onProductionStart(final ApplicationFlowStateChangedEvent evt) {
		if (evt.getCurrentState().equals(ApplicationFlowState.STT_STARTING)) {
				offlineCounting.processOfflineCounting();
		}
	}
}