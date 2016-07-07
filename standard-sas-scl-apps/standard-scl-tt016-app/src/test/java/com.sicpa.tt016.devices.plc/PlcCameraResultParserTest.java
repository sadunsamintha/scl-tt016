package com.sicpa.tt016.devices.plc;

import com.sicpa.tt016.model.PlcCameraProductStatus;
import com.sicpa.tt016.model.PlcCameraResultEvent;
import junit.framework.Assert;
import org.junit.Test;

public class PlcCameraResultParserTest {

	@Test
	public void plcCameraResultGoodArgument1() {
		PlcCameraResultEvent event = PlcCameraResultParser.getPlcCameraResultEvent(0x00AABB02);

		Assert.assertEquals(170, event.getIndex());
		Assert.assertEquals(187, event.getDecodeTimeMs());
		Assert.assertEquals(PlcCameraProductStatus.UNREADABLE, event.getPlcCameraProductStatus());
	}

	@Test
	public void plcCameraResultGoodArgument2() {
		PlcCameraResultEvent event = PlcCameraResultParser.getPlcCameraResultEvent(0x0010101);

		Assert.assertEquals(1, event.getIndex());
		Assert.assertEquals(1, event.getDecodeTimeMs());
		Assert.assertEquals(PlcCameraProductStatus.GOOD, event.getPlcCameraProductStatus());
	}

	@Test
	public void plcCameraResultGoodArgument3() {
		PlcCameraResultEvent event = PlcCameraResultParser.getPlcCameraResultEvent(0x788d5603);

		Assert.assertEquals(255, event.getIndex());
		Assert.assertEquals(1, event.getDecodeTimeMs());
		Assert.assertEquals(PlcCameraProductStatus.GOOD, event.getPlcCameraProductStatus());
	}

	@Test
	public void plcCameraResultNonExistingProductStatus() {
		try {
			PlcCameraResultParser.getPlcCameraResultEvent(0x00010122);
		} catch (IllegalArgumentException e) {
			Assert.assertEquals("No product status for supplied id: 34", e.getMessage());
		}
	}
}
