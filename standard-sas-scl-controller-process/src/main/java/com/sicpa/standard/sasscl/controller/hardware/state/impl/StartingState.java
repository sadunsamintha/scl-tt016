package com.sicpa.standard.sasscl.controller.hardware.state.impl;

import com.sicpa.standard.sasscl.controller.hardware.HardwareControllerStatus;
import com.sicpa.standard.sasscl.controller.hardware.HardwareControllerStatusEvent;
import com.sicpa.standard.sasscl.controller.hardware.IHardwareControllerState;
import com.sicpa.standard.sasscl.controller.hardware.state.AbstractHardwareControllerState;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.devices.DeviceStatusEvent;

public class StartingState extends AbstractHardwareControllerState {

	protected IHardwareControllerState startedState;
	protected IHardwareControllerState disconnectingState;
	protected IHardwareControllerState connectingState;

	public StartingState() {
	}

	@Override
	public void enter() {
		super.enter();
		fireStatusChanged(new HardwareControllerStatusEvent(HardwareControllerStatus.STARTING));
		startDevices();
	}

	@Override
	public void deviceStatusChanged(DeviceStatusEvent evt) {
		if (evt.getStatus().equals(DeviceStatus.DISCONNECTED)) {
			setNextState(connectingState);
		} else if (evt.getStatus().equals(DeviceStatus.STARTED)) {
			if (checkAllStartableDeviceStatus(true, DeviceStatus.STARTED)) {
				setNextState(startedState);
			}
		}
	}

	@Override
	public void errorMessageAdded() {
		setNextState(connectingState);
	}

	@Override
	public void disconnect() {
		// should only be called from the shutdownhook
		setNextState(disconnectingState);
	}

	public IHardwareControllerState getStartedState() {
		return startedState;
	}

	public void setStartedState(IHardwareControllerState startedState) {
		this.startedState = startedState;
	}

	public IHardwareControllerState getDisconnectingState() {
		return disconnectingState;
	}

	public void setDisconnectingState(IHardwareControllerState disconnectingState) {
		this.disconnectingState = disconnectingState;
	}

	public IHardwareControllerState getConnectingState() {
		return connectingState;
	}

	public void setConnectingState(IHardwareControllerState connectingState) {
		this.connectingState = connectingState;
	}
}
