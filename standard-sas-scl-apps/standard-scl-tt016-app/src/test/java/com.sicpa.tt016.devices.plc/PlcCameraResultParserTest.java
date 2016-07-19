package com.sicpa.tt016.devices.plc;

import com.sicpa.tt016.model.PlcCameraProductStatus;
import com.sicpa.tt016.model.PlcCameraResult;
import com.sicpa.tt016.model.event.PlcCameraResultEvent;
import junit.framework.Assert;
import org.junit.Test;

public class PlcCameraResultParserTest {

	@Test
	public void plcCameraResultGoodArgument1() {
		PlcCameraResult plcCameraResult = PlcCameraResultParser.getPlcCameraResultEvent(0x00AABB02);

		Assert.assertEquals(170, plcCameraResult.getIndex());
		Assert.assertEquals(187, plcCameraResult.getDecodeTimeMs());
		Assert.assertEquals(PlcCameraProductStatus.UNREADABLE, plcCameraResult.getPlcCameraProductStatus());
	}

	@Test
	public void plcCameraResultGoodArgument2() {
		PlcCameraResult plcCameraResult = PlcCameraResultParser.getPlcCameraResultEvent(0x0010101);

		Assert.assertEquals(1, plcCameraResult.getIndex());
		Assert.assertEquals(1, plcCameraResult.getDecodeTimeMs());
		Assert.assertEquals(PlcCameraProductStatus.GOOD, plcCameraResult.getPlcCameraProductStatus());
	}

	@Test
	public void plcCameraResultGoodArgument3() {
		PlcCameraResult plcCameraResult = PlcCameraResultParser.getPlcCameraResultEvent(0x788d5603);

		Assert.assertEquals(141, plcCameraResult.getIndex());
		Assert.assertEquals(86, plcCameraResult.getDecodeTimeMs());
		Assert.assertEquals(PlcCameraProductStatus.NO_INK, plcCameraResult.getPlcCameraProductStatus());
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
