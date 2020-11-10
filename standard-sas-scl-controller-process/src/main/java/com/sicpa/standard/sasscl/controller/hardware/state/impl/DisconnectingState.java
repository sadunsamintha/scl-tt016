package com.sicpa.standard.sasscl.controller.hardware.state.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.sasscl.controller.hardware.HardwareControllerStatus;
import com.sicpa.standard.sasscl.controller.hardware.HardwareControllerStatusEvent;
import com.sicpa.standard.sasscl.controller.hardware.IHardwareControllerState;
import com.sicpa.standard.sasscl.controller.hardware.state.AbstractHardwareControllerState;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.devices.DeviceStatusEvent;

public class DisconnectingState extends AbstractHardwareControllerState {
	
	private final static Logger logger = LoggerFactory.getLogger(DisconnectingState.class);

	protected IHardwareControllerState disconnectedState;

	@Override
	public void enter() {
		logger.info("Entering Disconnecting State");
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
