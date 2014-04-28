package com.sicpa.standard.sasscl.controller.hardware.state.impl;

import com.sicpa.standard.sasscl.controller.hardware.HardwareControllerStatus;
import com.sicpa.standard.sasscl.controller.hardware.HardwareControllerStatusEvent;
import com.sicpa.standard.sasscl.controller.hardware.IHardwareControllerState;
import com.sicpa.standard.sasscl.controller.hardware.state.AbstractHardwareControllerState;
import com.sicpa.standard.sasscl.devices.DeviceStatusEvent;

public class ConnectingState extends AbstractHardwareControllerState {

	protected IHardwareControllerState connectedState;
	protected IHardwareControllerState disconnectingState;

	@Override
	public void enter() {
		super.enter();
		fireStatusChanged(new HardwareControllerStatusEvent(HardwareControllerStatus.CONNECTING, getErrorMessages()));
		if (!isRecovering()) {
			connectDevices();
		} 
	}

	@Override
	public void deviceStatusChanged(DeviceStatusEvent evt) {
		moveToNextStateIfReady();
	}

	protected void moveToNextStateIfReady() {
		if (areDevicesReady()) {
			setter.setCurrentState(connectedState);
		} else {
			// to update the error message on the screen
			fireStatusChanged(new HardwareControllerStatusEvent(HardwareControllerStatus.CONNECTING, getErrorMessages()));
		}
	}

	@Override
	public void disconnect() {
		setNextState(disconnectingState);
	}

	@Override
	public void stop() {
	}

	public IHardwareControllerState getConnectedState() {
		return connectedState;
	}

	public void setConnectedState(IHardwareControllerState connectedState) {
		this.connectedState = connectedState;
	}

	public IHardwareControllerState getDisconnectingState() {
		return disconnectingState;
	}

	public void setDisconnectingState(IHardwareControllerState disconnectingState) {
		this.disconnectingState = disconnectingState;
	}

	@Override
	public void errorMessageAdded() {
		moveToNextStateIfReady();

	}

	@Override
	public void errorMessageRemoved() {
		moveToNextStateIfReady();
	}
}
