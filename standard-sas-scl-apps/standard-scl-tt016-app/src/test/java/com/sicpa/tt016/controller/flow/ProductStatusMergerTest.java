package com.sicpa.tt016.controller.flow;

import com.sicpa.tt016.devices.plc.PlcCameraResultParser;
import junit.framework.Assert;
import org.junit.Test;

public class ProductStatusMergerTest {

	// 'A' is "41" in hexadecimal
	@Test
	public void codeComparatorLastByteSame1() {
		String cameraCode = "0000A";
		byte plcCameraCodeLastByte = PlcCameraResultParser.getPlcCameraResultLastByteEncryptedCode(0x41000000);

		Assert.assertEquals(true, ProductStatusMerger.CodeComparator.isEqual(cameraCode, plcCameraCodeLastByte));
	}

	// '1' is "31" in hexadecimal
	@Test
	public void codeComparatorLastByteSame2() {
		String cameraCode = "00001";
		byte plcCameraCodeLastByte = PlcCameraResultParser.getPlcCameraResultLastByteEncryptedCode(0x31000000);

		Assert.assertEquals(true, ProductStatusMerger.CodeComparator.isEqual(cameraCode, plcCameraCodeLastByte));
	}

	// '?' is "3F" in hexadecimal
	@Test
	public void codeComparatorLastByteSame3() {
		String cameraCode = "0000?";
		byte plcCameraCodeLastByte = PlcCameraResultParser.getPlcCameraResultLastByteEncryptedCode(0x3F000000);

		Assert.assertEquals(true, ProductStatusMerger.CodeComparator.isEqual(cameraCode, plcCameraCodeLastByte));
	}
}
