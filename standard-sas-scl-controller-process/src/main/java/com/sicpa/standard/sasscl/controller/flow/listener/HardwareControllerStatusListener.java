package com.sicpa.standard.sasscl.controller.flow.listener;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.sasscl.controller.flow.IFlowControl;
import com.sicpa.standard.sasscl.controller.hardware.HardwareControllerStatus;
import com.sicpa.standard.sasscl.controller.hardware.HardwareControllerStatusEvent;

public class HardwareControllerStatusListener {

	protected IFlowControl flowControl;

	@Subscribe
	public void hardwareControllerStatusChanged(HardwareControllerStatusEvent evt) {
		HardwareControllerStatus status = evt.getStatus();
		if (status.equals(HardwareControllerStatus.CONNECTED)) {
			flowControl.notifyHardwareControllerConnected();
		} else if (status.equals(HardwareControllerStatus.STARTED)) {
			flowControl.notifyHardwareControllerStarted();
		} else if (status.equals(HardwareControllerStatus.STOPPING)) {
			flowControl.notifyHardwareControllerStopping();
		} else if (status.equals(HardwareControllerStatus.DISCONNECTED)) {
			flowControl.notifyHardwareControllerDisconnected();
		} else if (status.equals(HardwareControllerStatus.CONNECTING)) {
			flowControl.notifyRecoveringConnection();
		}
	}

	public void setFlowControl(IFlowControl flowControl) {
		this.flowControl = flowControl;
	}
}
