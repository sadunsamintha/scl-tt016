package com.sicpa.standard.sasscl.eventbus;

import com.sicpa.standard.client.common.eventbus.impl.EventHandler;
import com.sicpa.standard.client.common.eventbus.service.EventBus;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.messages.MessageEventKey;

public class EventBusWithUncaughtExceptionHandling extends EventBus {
	
	public EventBusWithUncaughtExceptionHandling(){
		delegate = new com.sicpa.standard.client.common.eventbus.impl.EventBus(){
			
			@Override
			protected void handleErrorCallWhenCallingListener(Object event,
					EventHandler wrapper, Exception e) {
				super.handleErrorCallWhenCallingListener(event, wrapper, e);
				EventBusService.post(new MessageEvent(MessageEventKey.FlowControl.UNCAUGHT_EXCEPTION));
			}
		};
	}
	
}
