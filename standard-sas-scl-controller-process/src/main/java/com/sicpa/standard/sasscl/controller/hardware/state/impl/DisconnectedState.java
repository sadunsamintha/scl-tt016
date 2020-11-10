package com.sicpa.standard.sasscl.controller.hardware.state.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.sasscl.controller.hardware.HardwareControllerStatus;
import com.sicpa.standard.sasscl.controller.hardware.HardwareControllerStatusEvent;
import com.sicpa.standard.sasscl.controller.hardware.IHardwareControllerState;
import com.sicpa.standard.sasscl.controller.hardware.state.AbstractHardwareControllerState;

public class DisconnectedState extends AbstractHardwareControllerState {
	
	private static final Logger logger = LoggerFactory.getLogger(DisconnectedState.class);

	protected IHardwareControllerState connectingState;

	public DisconnectedState() {
	}

	@Override
	public void enter() {
		logger.info("Entering Disconnected State");
		super.enter();
		if (deviceErrorRepository != null) {
			// is null during initialisation, so important to keep it
			deviceErrorRepository.reset();
		}
		fireStatusChanged(new HardwareControllerStatusEvent(HardwareControllerStatus.DISCONNECTED));
	}

	@Override
	public void disconnect() {
	}

	@Override
	public void connect() {
		setNextState(connectingState);
	}

	public void setConnectingState(IHardwareControllerState connectingState) {
		this.connectingState = connectingState;
	}

	public IHardwareControllerState getConnectingState() {
		return connectingState;
	}
}
