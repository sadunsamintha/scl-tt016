package com.sicpa.tt016.devices.plc;

import com.sicpa.tt016.model.PlcCameraProductStatus;
import com.sicpa.tt016.model.PlcCameraResultEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlcCameraResultParser {

	protected final static Logger logger = LoggerFactory.getLogger(PlcCameraResultParser.class);

	private static int PLC_CAMERA_RESULT_LENGTH = 8;
	private static int INDEX_BEGIN_INDEX = 2;
	private static int DECODING_TIME_BEGIN_INDEX = 4;
	private static int PRODUCT_STATUS_BEGIN_INDEX = 6;

	/**
	 * Method that parses the camera result received by the application from the PLC and returns an instance
	 * representing such information.
	 * <br><br>
	 * An exception IllegalArgumentException is thrown if supplied String argument is null, blank or not of the
	 * correct length.
	 *
	 * @param plcCameraResult Camera result sent by the PLC. The argument is a Sring  with the following format:
	 *                        '00AABBCC'. Where all parts are hexadecimal values and where AA - is the index of the
	 *                        result; BB is the decoding time in milliseconds and CC is the product status.
	 * @return The instance representing the PLC camera result
	 */
	public static PlcCameraResultEvent getPlcCameraResultEvent(String plcCameraResult) {
		logger.debug("PLC camera result received: {}", plcCameraResult);

		checkNull(plcCameraResult);
		checkResultOfCorrectLength(plcCameraResult);

		int index = Integer.parseInt(plcCameraResult.substring(INDEX_BEGIN_INDEX, DECODING_TIME_BEGIN_INDEX), 16);
		int decodingTime = Integer.parseInt(plcCameraResult.substring(DECODING_TIME_BEGIN_INDEX,
				PRODUCT_STATUS_BEGIN_INDEX), 16);
		int productStatus = Integer.parseInt(plcCameraResult.substring(PRODUCT_STATUS_BEGIN_INDEX), 16);

		return new PlcCameraResultEvent(index, decodingTime, PlcCameraProductStatus.valueOf(productStatus));
	}

	private static void checkResultOfCorrectLength(String plcCameraResult) {
		if (plcCameraResult.length() != PLC_CAMERA_RESULT_LENGTH) {
			throw new IllegalArgumentException("Illegal PLC camera result length. Expected 8, got: " +
					plcCameraResult.length());
		}
	}

	private static void checkNull(String plcCameraResult) {
		if (plcCameraResult == null) {
			throw new IllegalArgumentException("PLC camera result cannot be null");
		}
	}


}
