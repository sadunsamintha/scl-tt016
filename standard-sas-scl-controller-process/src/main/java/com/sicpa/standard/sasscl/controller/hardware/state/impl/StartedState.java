package com.sicpa.standard.sasscl.controller.hardware.state.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.controller.hardware.HardwareControllerStatus;
import com.sicpa.standard.sasscl.controller.hardware.HardwareControllerStatusEvent;
import com.sicpa.standard.sasscl.controller.hardware.IHardwareControllerState;
import com.sicpa.standard.sasscl.controller.hardware.state.AbstractHardwareControllerState;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.devices.DeviceStatusEvent;
import com.sicpa.standard.sasscl.devices.IDevice;
import com.sicpa.standard.sasscl.devices.IStartableDevice;
import com.sicpa.standard.sasscl.messages.MessageEventKey;

public class StartedState extends AbstractHardwareControllerState {
	
	private static final Logger logger = LoggerFactory.getLogger(StartedState.class);

	protected IHardwareControllerState stoppingState;
	protected IHardwareControllerState disconnectingState;

	public StartedState() {
	}

	@Override
	public void enter() {
		logger.info("Entering Started State");
		super.enter();
		startPlc();
		fireStatusChanged(new HardwareControllerStatusEvent(HardwareControllerStatus.STARTED));
	}

	@Override
	public void stop() {
		setNextState(stoppingState);
	}

	@Override
	public void deviceStatusChanged(DeviceStatusEvent evt) {
		if (evt.getStatus().equals(DeviceStatus.DISCONNECTED)) {
			if (allProductionBlockableDevicesConnected()) {
				sendDeviceDisconnectWarning(evt.getDevice());
			} else {
				setNextState(stoppingState);
			}
		}

		if (evt.getStatus().equals(DeviceStatus.CONNECTED) && (evt.getDevice() != null && evt.getDevice().getName().toUpperCase().equals("BRS"))) {
			for (IStartableDevice device : startableDevices) {
				if (!device.isBlockProductionStart() && (device.getName() != null && device.getName().toUpperCase().equals("BRS"))){
	            	if (device.getStatus().isConnected()) {
	            		startDevice(device);
	            	}
	            }
			}
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

	private void sendDeviceDisconnectWarning(IDevice device) {
		EventBusService.post(
				new MessageEvent(this, MessageEventKey.DevicesController.DEVICE_DISCONNECT_WARNING, device.getName()));
	}
}

