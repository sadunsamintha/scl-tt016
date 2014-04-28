package com.sicpa.standard.sasscl.devices.plc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.sicpa.standard.plc.value.IPlcVariable;
import com.sicpa.standard.plc.value.PlcVariable;

public class PlcMessageDescriptorTest {

	private static final String MESSAGE_KEY = "MESSAGE_KEY";
	private static final IPlcVariable<Boolean> PLC_VARIABLE = PlcVariable.createBooleanVar("ITEM1");
	private PlcMessageDescriptor descriptor;
	
	@Before
	public void setUp() throws Exception {
		descriptor = new PlcMessageDescriptor();
	}


	@Test
	public void testPlcMessageDescriptorIPlcVariableOfQStringBoolean() {
		descriptor = new PlcMessageDescriptor(PLC_VARIABLE,MESSAGE_KEY,true);
		assertEquals(PLC_VARIABLE,descriptor.getPlcVariable());
		assertEquals(MESSAGE_KEY, descriptor.getMessageKey());
		assertTrue(descriptor.isBlockable());
	}

	@Test
	public void testSetGet() {
		descriptor.setBlockable(true);
		descriptor.setMessageKey(MESSAGE_KEY);
		descriptor.setPlcVariable(PLC_VARIABLE);
		
		assertEquals(PLC_VARIABLE,descriptor.getPlcVariable());
		assertEquals(MESSAGE_KEY, descriptor.getMessageKey());
		assertTrue(descriptor.isBlockable());
	}

	

}
