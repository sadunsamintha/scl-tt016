package com.sicpa.standard.sasscl.controller.message;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.sasscl.messages.ActionEventIgnore;

public class MessageHandlerIgnore {

	@Subscribe
	public void handleMessage(ActionEventIgnore ignoreEvent) {

	}
}
