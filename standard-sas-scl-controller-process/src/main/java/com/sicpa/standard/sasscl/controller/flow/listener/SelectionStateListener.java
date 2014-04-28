package com.sicpa.standard.sasscl.controller.flow.listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.sasscl.config.GlobalBean;
import com.sicpa.standard.sasscl.controller.device.IPlcIndependentDevicesController;
import com.sicpa.standard.sasscl.controller.device.group.DevicesGroup;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;

public class SelectionStateListener {

	protected final List<ApplicationFlowState> selectionStates = new ArrayList<ApplicationFlowState>(Arrays.asList(
			ApplicationFlowState.STT_SELECT_NO_PREVIOUS, ApplicationFlowState.STT_SELECT_WITH_PREVIOUS));

	protected IPlcIndependentDevicesController devicesController;
	protected GlobalBean globalBean;

	protected boolean started = false;

	@Subscribe
	public void processStateChanged(ApplicationFlowStateChangedEvent evt) {
		if (!globalBean.isUseBarcodeReader()) {
			return;
		}

		if (selectionStates.contains(evt.getCurrentState())) {
			devicesController.startDevicesGroup(DevicesGroup.SELECTION_SCREEN_GROUP);
			started = true;
		} else if (started) {
			devicesController.stopDevicesGroup(DevicesGroup.SELECTION_SCREEN_GROUP);
			started = false;
		}
	}

	public void setDevicesController(IPlcIndependentDevicesController devicesController) {
		this.devicesController = devicesController;
	}

	public void setGlobalBean(GlobalBean globalBean) {
		this.globalBean = globalBean;
	}
}
