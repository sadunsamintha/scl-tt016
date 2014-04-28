package com.sicpa.standard.sasscl.skucheck;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;

public class AcquisitionStatistics extends
		com.sicpa.standard.sasscl.skucheck.acquisition.statistics.AcquisitionStatistics {

	@Subscribe
	public void handleApplicationStatusChanged(ApplicationFlowStateChangedEvent evt) {
		if (evt.getCurrentState().equals(ApplicationFlowState.STT_STARTING)) {
			reset();
		}
	}

}
