package com.sicpa.standard.sasscl.controller.message;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.sasscl.messages.ActionEventLog;

public class MessageHandlerLog {

	private static final Logger logger = LoggerFactory.getLogger(MessageHandlerLog.class);

	@Subscribe
	public void handleMessage(ActionEventLog logEvent) {
		logger.info(logEvent.getKey() + Arrays.toString(logEvent.getParams()));
	}
}
