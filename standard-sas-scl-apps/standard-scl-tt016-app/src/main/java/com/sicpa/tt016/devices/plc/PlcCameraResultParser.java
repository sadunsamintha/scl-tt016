package com.sicpa.tt016.devices.plc;

import com.sicpa.tt016.model.PlcCameraProductStatus;
import com.sicpa.tt016.model.PlcCameraResultEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

public class PlcCameraResultParser {

	/**
	 * Method that parses the camera result received by the application from the PLC and returns an instance
	 * representing such information.
	 * <br><br>
	 * An exception IllegalArgumentException is thrown if supplied String argument is null, blank or not of the
	 * correct length.
	 *
	 * @param plcCameraResult Camera result sent by the PLC. The argument is an int where each byte corresponds to a
	 *                        value. E.g.: 00AABBCC; 00 is the last byte of the encrypted code; AA - is the index of
	 *                        the result; BB is the decoding time in milliseconds and CC is the product status.
	 * @return The instance representing the PLC camera result
	 */
	public static PlcCameraResultEvent getPlcCameraResultEvent(int plcCameraResult) {
		byte[] plcCameraResults = ByteBuffer.allocate(4).putInt(plcCameraResult).array();

		int encryptedCodeLastByte = plcCameraResults[0] & 0xFF;
		int index = plcCameraResults[1] & 0xFF;
		int decodingTime = plcCameraResults[2] & 0xFF;
		int productStatus = plcCameraResults[3] & 0xFF;

		return new PlcCameraResultEvent(encryptedCodeLastByte, index, decodingTime, PlcCameraProductStatus.valueOf
				(productStatus));
	}

	public static int getPlcCameraResultIndex(int plcCameraResult) {
		byte[] plcCameraResults = ByteBuffer.allocate(4).putInt(plcCameraResult).array();

		return plcCameraResults[1] & 0xFF;
	}


}
