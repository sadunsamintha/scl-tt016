package com.sicpa.standard.sasscl.controller.hardware.state.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.sasscl.controller.hardware.HardwareControllerStatus;
import com.sicpa.standard.sasscl.controller.hardware.HardwareControllerStatusEvent;
import com.sicpa.standard.sasscl.controller.hardware.IHardwareControllerState;
import com.sicpa.standard.sasscl.controller.hardware.state.AbstractHardwareControllerState;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.devices.DeviceStatusEvent;

public class StoppingState extends AbstractHardwareControllerState {
	
	private final static Logger logger = LoggerFactory.getLogger(StoppingState.class);

	protected IHardwareControllerState connectedState;
	protected IHardwareControllerState connectingState;

	public void enter() {
		logger.info("Entering Stopping State");
		super.enter();
		fireStatusChanged(new HardwareControllerStatusEvent(HardwareControllerStatus.STOPPING));
		stopDevices();
	}

	@Override
	public void stop() {
	}

	@Override
	public void deviceStatusChanged(DeviceStatusEvent evt) {
		if (evt.getStatus().equals(DeviceStatus.STOPPED) || evt.getStatus().equals(DeviceStatus.DISCONNECTED)) {
			if (areAllDevicesStopped()) {
				// if one device is not connected , try to connect it again
				if (areDevicesReady()) {
					setNextState(connectedState);
				} else {
					setNextState(connectingState);
				}
			}
		}
	}

	public IHardwareControllerState getConnectedState() {
		return connectedState;
	}

	public void setConnectedState(IHardwareControllerState connectedState) {
		this.connectedState = connectedState;
	}

	public IHardwareControllerState getConnectingState() {
		return connectingState;
	}

	public void setConnectingState(IHardwareControllerState connectingState) {
		this.connectingState = connectingState;
	}

}
