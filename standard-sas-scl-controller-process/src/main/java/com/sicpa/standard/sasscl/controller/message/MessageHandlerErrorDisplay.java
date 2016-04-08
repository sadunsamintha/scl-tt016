package com.sicpa.standard.sasscl.controller.message;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.controller.view.event.ErrorViewEvent;
import com.sicpa.standard.sasscl.messages.ActionEventErrorDisplay;

public class MessageHandlerErrorDisplay {

	private static final Logger logger = LoggerFactory.getLogger(MessageHandlerWarning.class);

	@Subscribe
	public void handleMessage(ActionEventErrorDisplay error) {
		logger.info("key="+error.getKey() +"; params:"+ Arrays.toString(error.getParams()));
		EventBusService.post(new ErrorViewEvent(error.getKey(), null, true, error.getParams()));
	}

}
