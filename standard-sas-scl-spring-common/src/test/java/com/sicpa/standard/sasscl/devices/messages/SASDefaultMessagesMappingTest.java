package com.sicpa.standard.sasscl.devices.messages;

import junit.framework.Assert;

import org.junit.Test;

import com.sicpa.standard.sasscl.messages.ActionMessageType;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.messages.SASDefaultMessagesMapping;

public class SASDefaultMessagesMappingTest {

	@Test
	public void testSASDefaultMessagesMapping() {
		SASDefaultMessagesMapping mapping = new SASDefaultMessagesMapping();
		Assert.assertEquals(ActionMessageType.ERROR, mapping.getMessageType(MessageEventKey.Alert.TOO_MUCH_CAMERA_ERROR));
		
	}
	
}
