package com.sicpa.standard.sasscl.controller.hardware.state.impl;

import com.sicpa.standard.sasscl.controller.hardware.HardwareControllerStatus;
import com.sicpa.standard.sasscl.controller.hardware.HardwareControllerStatusEvent;
import com.sicpa.standard.sasscl.controller.hardware.IHardwareControllerState;
import com.sicpa.standard.sasscl.controller.hardware.state.AbstractHardwareControllerState;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.devices.DeviceStatusEvent;

public class ConnectedState extends AbstractHardwareControllerState {

	protected IHardwareControllerState connectingState;
	protected IHardwareControllerState disconnectingState;
	protected IHardwareControllerState startingState;

	public ConnectedState() {
	}

	@Override
	public void enter() {
		super.enter();
		fireStatusChanged(new HardwareControllerStatusEvent(HardwareControllerStatus.CONNECTED));
	}

	@Override
	public void disconnect() {
		setNextState(disconnectingState);
	}

	@Override
	public void deviceStatusChanged(DeviceStatusEvent evt) {
		if (evt.getStatus().equals(DeviceStatus.DISCONNECTED)) {
			setNextState(connectingState);
		}
	}

	@Override
	public void start() {
		setNextState(startingState);
	}

	public IHardwareControllerState getDisconnectingState() {
		return disconnectingState;
	}

	public void setDisconnectingState(IHardwareControllerState disconnectingState) {
		this.disconnectingState = disconnectingState;
	}

	public IHardwareControllerState getStartingState() {
		return startingState;
	}

	public void setStartingState(IHardwareControllerState startingState) {
		this.startingState = startingState;
	}

	public IHardwareControllerState getConnectingState() {
		return connectingState;
	}

	public void setConnectingState(IHardwareControllerState connectingState) {
		this.connectingState = connectingState;
	}

	@Override
	public void errorMessageAdded() {
		setNextState(connectingState);
	}
}
