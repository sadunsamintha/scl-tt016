package com.sicpa.standard.sasscl.common.exception;

import junit.framework.Assert;

import org.junit.Test;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.messages.MessageEventKey;

public class ExceptionHandlerTest {

	boolean eventCatched = false;

	@Test
	public void testException() throws InterruptedException {

		Object EventCatcher = new Object() {
			@SuppressWarnings("unused")
			@Subscribe
			public void handleEvent(MessageEvent evt) {
				if (evt.getKey().equals(MessageEventKey.FlowControl.UNCAUGHT_EXCEPTION)) {
					eventCatched = true;
				}
			}
		};

		EventBusService.register(EventCatcher);

		new ExceptionHandler();

		Thread warningThrower = new Thread(new Runnable() {

			@Override
			public void run() {
				throw new NullPointerException();
			}
		});
		warningThrower.start();
		warningThrower.join();

		Assert.assertTrue(eventCatched);

	}
}
