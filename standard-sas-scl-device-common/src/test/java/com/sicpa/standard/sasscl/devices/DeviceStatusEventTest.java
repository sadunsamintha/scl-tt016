package com.sicpa.standard.sasscl.devices;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

public class DeviceStatusEventTest {

	private IDevice device; 
	
	@Before
	public void setup(){
		device = mock(IDevice.class);
	}
	
	@Test
	public void testDeviceStatusEvent() throws Exception {
		DeviceStatusEvent deviceStatusEvent = new DeviceStatusEvent(DeviceStatus.CONNECTED, device);
		assertEquals(device,deviceStatusEvent.getDevice());
		assertEquals(DeviceStatus.CONNECTED,deviceStatusEvent.getStatus());
	}

}
