package com.sicpa.standard.sasscl.controller.flow.listener;

import static com.sicpa.standard.sasscl.messages.MessageEventKey.RemoteServer.MAX_DOWNTIME;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.controller.predicate.start.IStartProductionValidator;
import com.sicpa.standard.client.common.controller.predicate.start.NoStartReason;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.devices.remote.MaxDownTimeReachedEvent;
import com.sicpa.standard.sasscl.messages.MessageEventKey;

public class MaxRemoteServerDownTimeListener implements IStartProductionValidator {

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

	@Override
	public NoStartReason isPossibleToStartProduction() {
		NoStartReason res = new NoStartReason();
		if (remoteServerMaxDownTimeReached) {
			res.addReason(MAX_DOWNTIME);
		}
		return res;
	}

}
