package com.sicpa.standard.sasscl.devices.messages;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.sicpa.standard.client.common.messages.MessageEvent;

public class MessageEventTest {

	private static final Object[] PARAMS = new Object[0];
	private static final String KEY = "KEY";
	private static final String KEY2 = "KEY2";

	@Test
	public void testMessageEventAllParams() {
		MessageEvent messageEvent = new MessageEvent(this, KEY, PARAMS);
		assertEquals(this, messageEvent.getSource());
		assertArrayEquals(PARAMS, messageEvent.getParams());
		assertEquals(KEY, messageEvent.getKey());
	}

	@Test
	public void testMessageEventKeyParams() {
		MessageEvent messageEvent = new MessageEvent(this, KEY, PARAMS);
		assertArrayEquals(PARAMS, messageEvent.getParams());
		assertEquals(KEY, messageEvent.getKey());
	}

	@Test
	public void testMessageEventKey() {
		MessageEvent messageEvent = new MessageEvent(KEY);
		assertNull(messageEvent.getParams());
		assertEquals(KEY, messageEvent.getKey());
	}

	@Test
	public void testSetKey() {
		MessageEvent messageEvent = new MessageEvent(KEY);
		messageEvent.setKey(KEY2);
		assertNull(messageEvent.getParams());
		assertEquals(KEY2, messageEvent.getKey());
	}
}
