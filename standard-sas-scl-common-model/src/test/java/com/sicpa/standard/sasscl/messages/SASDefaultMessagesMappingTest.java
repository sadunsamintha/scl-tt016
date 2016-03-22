package com.sicpa.standard.sasscl.messages;

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.junit.Test;

import com.sicpa.standard.sasscl.messages.MessageEventKey.Simulator;

public class SASDefaultMessagesMappingTest {

	@Test
	public void testOverrideEventType() throws Exception {
		SASDefaultMessagesMapping mapping = new SASDefaultMessagesMapping();

		ActionMessageType type = (ActionMessageType) mapping.getMessageType(Simulator.CAMERA);
		System.out.println(type);
		assertEquals(ActionMessageType.WARNING, type);

		Properties prop = new Properties();
		prop.put(SASDefaultMessagesMapping.PREFIX_OVERRIDING_PROPERTIES + Simulator.CAMERA, "ActionEventErrorDisplay");

		mapping.setOverridingProperties(prop);
		mapping.init();

		type = (ActionMessageType) mapping.getMessageType(Simulator.CAMERA);
		System.out.println(type);
		assertEquals(ActionEventErrorDisplay.class, type.getActionEventClass());

	}

}
