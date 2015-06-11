package com.sicpa.standard.sasscl.devices.messages;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import com.sicpa.standard.client.common.messages.IMessagesMapping;
import com.sicpa.standard.client.common.messages.MessageType;
import com.sicpa.standard.client.common.messages.MessagesUtils;

public class MessagesUtilsTest {

	private static final String KEY = "KEY";

	private MessagesUtils messagesUtils;
	
	private IMessagesMapping mapping;
	
	@Before
	public void setUp() throws Exception {
		messagesUtils = new MessagesUtils();
		mapping = mock(IMessagesMapping.class);
		
		messagesUtils.setMessagesMapping(mapping);
	}

	@Test
	public void testGetType() {
		when(mapping.getMessageType(KEY)).thenReturn(MessageType.ERROR);
		
		assertEquals(MessageType.ERROR,MessagesUtils.getType(KEY));
	}

	@Test
	public void testGetMessage() {
		assertEquals("<"+KEY+">",MessagesUtils.getMessage(KEY));
	}

	@Test
	public void testGetMessageForGUI() {
		assertEquals(KEY+ " <"+KEY+">",MessagesUtils.getMessageForGUI(KEY));
	}

}
