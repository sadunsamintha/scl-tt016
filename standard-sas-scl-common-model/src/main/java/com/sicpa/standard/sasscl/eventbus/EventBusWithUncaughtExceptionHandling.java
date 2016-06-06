package com.sicpa.standard.sasscl.eventbus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.eventbus.impl.EventHandler;
import com.sicpa.standard.client.common.eventbus.service.EventBus;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.messages.MessageEventKey;

public class EventBusWithUncaughtExceptionHandling extends EventBus {

	private static final Logger logger = LoggerFactory.getLogger(EventBusWithUncaughtExceptionHandling.class);

	public EventBusWithUncaughtExceptionHandling() {
		delegate = new com.sicpa.standard.client.common.eventbus.impl.EventBus() {

			@Override
			protected void handleErrorCallWhenCallingListener(Object event, EventHandler wrapper, Exception e) {
				super.handleErrorCallWhenCallingListener(event, wrapper, e);
				EventBusWithUncaughtExceptionHandling.logger.error("", e);
				EventBusService.post(new MessageEvent(MessageEventKey.FlowControl.UNCAUGHT_EXCEPTION));
			}
		};
	}

}
