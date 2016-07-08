package com.sicpa.tt016.devices.plc;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;

import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_STARTING;

public class PlcCameraResultIndexManagerSimulator extends PlcCameraResultIndexManager {

	@Subscribe
	public void syncPlcCameraResultIndex(ApplicationFlowStateChangedEvent event) {
		if (event.getCurrentState().equals(STT_STARTING)) {
			previousIndex = 0;
		}
	}
}
