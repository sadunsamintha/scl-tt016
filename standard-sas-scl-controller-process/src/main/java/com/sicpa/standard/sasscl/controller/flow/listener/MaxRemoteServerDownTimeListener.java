package com.sicpa.standard.sasscl.controller.flow.listener;

import static com.sicpa.standard.sasscl.messages.MessageEventKey.RemoteServer.MAX_DOWNTIME;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.controller.process.IProductionStartValidator;
import com.sicpa.standard.sasscl.controller.process.ProductionStartValidatorResult;
import com.sicpa.standard.sasscl.devices.remote.MaxDownTimeReachedEvent;
import com.sicpa.standard.sasscl.messages.MessageEventKey;

public class MaxRemoteServerDownTimeListener implements IProductionStartValidator {

	protected boolean remoteServerMaxDownTimeReached;

	@Subscribe
	public void notifyMaxDownTimeReached(final MaxDownTimeReachedEvent event) {
		if (!remoteServerMaxDownTimeReached && event.isMaxDownTimeReached()) {
			// max down time is reach for the first time
			remoteServerMaxDownTimeReached = true;
			EventBusService.post(new MessageEvent(MessageEventKey.RemoteServer.MAX_DOWNTIME));
		} else {
			remoteServerMaxDownTimeReached = false;
		}
	}

	public ProductionStartValidatorResult validateStart() {
		if (remoteServerMaxDownTimeReached) {
			return ProductionStartValidatorResult.createInvalidResult(MAX_DOWNTIME);
		} else {
			return ProductionStartValidatorResult.createValidResult();
		}
	}

}
