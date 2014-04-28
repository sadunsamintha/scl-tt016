package com.sicpa.standard.sasscl.controller.hardware.state.impl;

import com.sicpa.standard.sasscl.controller.hardware.HardwareControllerStatus;
import com.sicpa.standard.sasscl.controller.hardware.HardwareControllerStatusEvent;
import com.sicpa.standard.sasscl.controller.hardware.IHardwareControllerState;
import com.sicpa.standard.sasscl.controller.hardware.state.AbstractHardwareControllerState;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.devices.DeviceStatusEvent;

public class StartedState extends AbstractHardwareControllerState {

	protected IHardwareControllerState stoppingState;
	protected IHardwareControllerState disconnectingState;

	public StartedState() {
	}

	@Override
	public void enter() {
		super.enter();
		fireStatusChanged(new HardwareControllerStatusEvent(HardwareControllerStatus.STARTED));
	}

	@Override
	public void stop() {
		setNextState(stoppingState);
	}

	@Override
	public void deviceStatusChanged(DeviceStatusEvent evt) {
		if (evt.getStatus().equals(DeviceStatus.DISCONNECTED)) {
			setNextState(stoppingState);
		}
	}

	@Override
	public void errorMessageAdded() {
		setNextState(stoppingState);
	}

	public IHardwareControllerState getStoppingState() {
		return stoppingState;
	}

	public void setStoppingState(IHardwareControllerState stoppingState) {
		this.stoppingState = stoppingState;
	}

	@Override
	public void disconnect() {
		// should only be called from the shutdownhook
		setNextState(disconnectingState);
	}

	public void setDisconnectingState(IHardwareControllerState disconnectingState) {
		this.disconnectingState = disconnectingState;
	}
}
