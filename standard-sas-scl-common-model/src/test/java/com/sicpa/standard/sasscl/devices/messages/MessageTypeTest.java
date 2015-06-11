/**
 * Author	: YYang
 * Date		: Nov 15, 2010
 *
 * Copyright (c) 2010 SICPA Security Solutions, all rights reserved.
 *
 */
package com.sicpa.standard.sasscl.devices.messages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.sicpa.standard.client.common.messages.MessageType;

public class MessageTypeTest {

	@Test
	public void testMessageTypeEqualMethod() {
		MessageType msgType = new MessageType("ERROR");
		assertEquals(msgType, MessageType.ERROR);
		assertFalse(MessageType.WARNING.equals(msgType));
	}
	
	@Test
	public void testToString() throws Exception {
		assertEquals("IGNORE",MessageType.IGNORE.toString());
	}
	
	@Test
	public void testHashCode() throws Exception {
		assertNotNull(MessageType.ERROR.hashCode());
		assertNotNull(new MessageType(null).hashCode());
		assertNotSame(MessageType.ERROR.hashCode(), new MessageType(null).hashCode());
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(MessageType.ERROR.equals(MessageType.ERROR));
		assertFalse(MessageType.ERROR.equals(MessageType.IGNORE));
		assertFalse(MessageType.ERROR.equals(null));
		assertFalse(MessageType.ERROR.equals(""));
		assertFalse(new MessageType(null).equals(MessageType.ERROR));
	}

}
