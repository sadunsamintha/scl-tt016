/**
 * Author	: YYang
 * Date		: Nov 15, 2010
 *
 * Copyright (c) 2010 SICPA Security Solutions, all rights reserved.
 *
 */
package com.sicpa.standard.sasscl.devices.messages;

import junit.framework.Assert;

import org.junit.Test;

import com.sicpa.standard.client.common.messages.DefaultMessagesMapping;
import com.sicpa.standard.client.common.messages.MessageType;

public class DefaultMessagesMappingTest {

	@Test
	public void testDefaultMessagesMapping() {

		DefaultMessagesMapping msgMapping = new DefaultMessagesMapping();
		msgMapping.addEntry("key1","code", MessageType.ERROR);
		msgMapping.addEntry("key2","code1" ,MessageType.WARNING);

		Assert.assertEquals(MessageType.ERROR, msgMapping.getMessageType("key1"));
		Assert.assertEquals(MessageType.WARNING, msgMapping.getMessageType("key2"));

	}

}
