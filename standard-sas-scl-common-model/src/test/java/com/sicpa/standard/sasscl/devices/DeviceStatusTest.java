package com.sicpa.standard.sasscl.devices;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DeviceStatusTest {

	private static final String DESCRIPTION = "DESCRIPTION";

	@Test
	public void testDeviceStatus() {
		DeviceStatus deviceStatus = new DeviceStatus(true, DESCRIPTION);
		assertTrue(deviceStatus.isConnected());
		assertEquals("device status:"+DESCRIPTION,deviceStatus.toString());
	}



}
