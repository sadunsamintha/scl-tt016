package com.sicpa.tt016.devices.plc;

import com.sicpa.tt016.model.PlcCameraProductStatus;
import com.sicpa.tt016.model.PlcCameraResult;
import com.sicpa.tt016.model.event.PlcCameraResultEvent;

import java.nio.ByteBuffer;

public class PlcCameraResultParser {

	/**
	 * Method that parses the camera result received by the application from the PLC and returns an instance
	 * representing such information.
	 * <br><br>
	 *
	 * @param plcCameraResult Camera result sent by the PLC. The argument is an int where each byte corresponds to a
	 *                        value. E.g.: 00AABBCC; 00 is the last byte of the encrypted code; AA - is the index of
	 *                        the result; BB is the decoding time in milliseconds and CC is the product status.
	 * @return The instance representing the PLC camera result
	 */
	public static PlcCameraResult getPlcCameraResultEvent(int plcCameraResult) {
		byte[] plcCameraResults = ByteBuffer.allocate(4).putInt(plcCameraResult).array();

		byte encryptedCodeLastByte = plcCameraResults[0];
		int index = plcCameraResults[1] & 0xFF;
		int decodingTime = plcCameraResults[2] & 0xFF;
		int productStatus = plcCameraResults[3] & 0xFF;

		return new PlcCameraResult(encryptedCodeLastByte, index, decodingTime, PlcCameraProductStatus.valueOf
				(productStatus));
	}

	public static int getPlcCameraResultIndex(int plcCameraResult) {
		byte[] plcCameraResults = ByteBuffer.allocate(4).putInt(plcCameraResult).array();

		return plcCameraResults[1] & 0xFF;
	}

	public static byte getPlcCameraResultLastByteEncryptedCode(int plcCameraResult) {
		byte[] plcCameraResults = ByteBuffer.allocate(4).putInt(plcCameraResult).array();

		return plcCameraResults[0];
	}
}
