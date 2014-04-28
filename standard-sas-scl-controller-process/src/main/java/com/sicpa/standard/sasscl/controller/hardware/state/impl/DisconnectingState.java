package com.sicpa.standard.sasscl.controller.hardware.state.impl;

import com.sicpa.standard.sasscl.controller.hardware.HardwareControllerStatus;
import com.sicpa.standard.sasscl.controller.hardware.HardwareControllerStatusEvent;
import com.sicpa.standard.sasscl.controller.hardware.IHardwareControllerState;
import com.sicpa.standard.sasscl.controller.hardware.state.AbstractHardwareControllerState;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.devices.DeviceStatusEvent;

public class DisconnectingState extends AbstractHardwareControllerState {

	protected IHardwareControllerState disconnectedState;

	@Override
	public void enter() {
		super.enter();
		fireStatusChanged(new HardwareControllerStatusEvent(HardwareControllerStatus.DISCONNECTING));
		disconnectDevices();
	}

	@Override
	public void deviceStatusChanged(DeviceStatusEvent evt) {
		if (evt.getStatus().equals(DeviceStatus.DISCONNECTED)) {
			if (checkAllDeviceStatus(false,DeviceStatus.DISCONNECTED)) {
				setNextState(disconnectedState);
			}
		}
	}

	@Override
	public void disconnect() {
	}

	public IHardwareControllerState getDisconnectedState() {
		return disconnectedState;
	}

	public void setDisconnectedState(IHardwareControllerState disconnectedState) {
		this.disconnectedState = disconnectedState;
	}

}
