package com.sicpa.standard.sasscl.devices.plc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class PlcRequestTest {

	private static final String DESCRIPTION = "DESCRIPTION";
	private static final int ID = 10;
	private PlcRequest plcRequest;
	private PlcRequest plcRequest2;
	
	@Before
	public void setUp() throws Exception {
		plcRequest = new PlcRequest(-1,"");
		plcRequest2 = new PlcRequest(ID,DESCRIPTION);
	}

	@Test
	public void testHashCodeByDefault() {
		assertEquals(-1,plcRequest.hashCode());
	}
	
	@Test
	public void testHashCode() {
		assertEquals(ID,plcRequest2.hashCode());
	}

	@Test
	public void testEqualsObjectDifferent() {
		assertFalse(plcRequest.equals(plcRequest2));
	}
	
	@Test
	public void testEqualsObject() {
		assertTrue(plcRequest.equals(new PlcRequest(-1,"")));
		assertFalse(plcRequest.equals(null));
		assertFalse(plcRequest.equals(""));
	}

	@Test
	public void testGetId() {
		assertEquals(-1,plcRequest.getId());
		assertEquals(ID,plcRequest2.getId());
	}

	@Test
	public void testGetDescription() {
		assertEquals("",plcRequest.getDescription());
		assertEquals(DESCRIPTION,plcRequest2.getDescription());
	}

}
