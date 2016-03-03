package com.sicpa.standard.sasscl.devices.plc.simulator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.plc.controller.IPlcController;
import com.sicpa.standard.sasscl.devices.plc.PlcAdaptorException;
import com.sicpa.standard.sasscl.devices.plc.event.PlcEvent;
import com.sicpa.standard.sasscl.devices.plc.impl.PlcSecureAdaptor;
import com.sicpa.standard.sasscl.event.LoginAttemptEvent;
import com.sicpa.standard.sasscl.security.UserId;

public class PlcSecureAdaptorSimulator extends PlcSecureAdaptor {

	private static final Logger logger = LoggerFactory.getLogger(PlcSecureAdaptorSimulator.class);

	public PlcSecureAdaptorSimulator(IPlcController<?> controller) {
		super(controller);
		EventBusService.register(this);
	}

	@Subscribe
	public void onLoginAttempt(LoginAttemptEvent event) {
		logger.debug("LoginAttemptEvent {}", event.getLogin());

		currentUserId = getUserId(event.getLogin());
		handleEvent(new PlcEvent(userAuthVarName, true));
	}

	@Override
	protected UserId checkPlcUser() throws PlcAdaptorException {
		if (currentUserId != null)
			return currentUserId;
		throw new PlcAdaptorException("User login invalid");
	}
}
