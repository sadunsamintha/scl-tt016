package com.sicpa.standard.sasscl.devices.plc;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PlcRequestTest {

	private static final String DESCRIPTION = "DESCRIPTION";
	private PlcRequest plcRequest;
	private PlcRequest plcRequest2;

	@Before
	public void setUp() throws Exception {
		plcRequest = new PlcRequest("");
		plcRequest2 = new PlcRequest(DESCRIPTION);
	}

	@Test
	public void testEqualsObjectDifferent() {
		assertFalse(plcRequest.equals(plcRequest2));
	}

	@Test
	public void testEqualsObject() {
		assertTrue(plcRequest.equals(new PlcRequest("")));
		assertFalse(plcRequest.equals(null));
		assertFalse(plcRequest.equals(""));
	}

	@Test
	public void testGetDescription() {
		assertEquals("", plcRequest.getDescription());
		assertEquals(DESCRIPTION, plcRequest2.getDescription());
	}

}
