package com.sicpa.standard.sasscl.controller.message;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.controller.view.event.ErrorViewEvent;
import com.sicpa.standard.sasscl.messages.ActionEventErrorDisplay;

public class MessageHandlerErrorDisplay {

	@Subscribe
	public void handleMessage(ActionEventErrorDisplay error) {
		EventBusService.post(new ErrorViewEvent(error.getKey(), null, true, error.getParams()));
	}

}
