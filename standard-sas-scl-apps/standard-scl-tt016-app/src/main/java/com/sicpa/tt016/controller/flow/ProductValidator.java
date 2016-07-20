package com.sicpa.tt016.controller.flow;

import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.ProductStatus;
import com.sicpa.tt016.model.PlcCameraProductStatus;
import com.sicpa.tt016.model.PlcCameraResult;
import com.sicpa.tt016.model.TT016ProductStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Validates whether the input product from camera and PLC are the same. A warning will be logged if the statuses
 * don't match or if the encrypted code of the product is not the same.
 */
public class ProductValidator {

	private static final Logger logger = LoggerFactory.getLogger(ProductValidator.class);

	private static Map<PlcCameraProductStatus, ProductStatus> statusesMapping;

	static {
		statusesMapping = new HashMap<>(4);
		statusesMapping.put(PlcCameraProductStatus.GOOD, ProductStatus.AUTHENTICATED);
		statusesMapping.put(PlcCameraProductStatus.UNREADABLE, ProductStatus.UNREAD);
		statusesMapping.put(PlcCameraProductStatus.NO_INK, ProductStatus.NO_INK);
		statusesMapping.put(PlcCameraProductStatus.EJECTED_PRODUCER, TT016ProductStatus.EJECTED_PRODUCER);
	}

	public static void validate(Product product, PlcCameraResult plcCameraResult) {
		ProductStatus productStatus = product.getStatus();
		PlcCameraProductStatus plcCameraProductStatus = plcCameraResult.getPlcCameraProductStatus();

		if (!isProductStatusEquals(plcCameraProductStatus, productStatus)) {
			logger.warn("Product status from PLC and camera not matching! plc:{}, camera:{}",
					plcCameraProductStatus.getDescription(), productStatus.toString());

		} else {
			if (isProductStatusValidFromPlcAndCamera(productStatus, plcCameraProductStatus)) {
				String cameraCode = product.getCode().getStringCode();
				byte plcLastByteCameraCode = plcCameraResult.getEncryptedCodeLastByte();

				if (!isCodeLastByteEquals(cameraCode, plcLastByteCameraCode)) {
					logger.warn("Encrypted code from PLC and camera not matching! plc:{}, camera:{}",
							plcLastByteCameraCode, cameraCode);
				}
			}
		}
	}

	/**
	 * Method that returns whether the product is considered valid or not (i.e.: code on the product was decoded
	 * successfully) by the PLC and camera.
	 *
	 * @return true if the product is considered valid by the two sources, false otherwise
	 */
	public static boolean isProductStatusValidFromPlcAndCamera(ProductStatus cameraProductStatus, PlcCameraProductStatus
			plcCameraProductStatus) {
		return cameraProductStatus.equals(ProductStatus.AUTHENTICATED) && plcCameraProductStatus.equals(
				PlcCameraProductStatus.GOOD);
	}

	/**
	 * Method used to compare the last byte of the encrypted code received from the camera with the last byte from the
	 * code received from the PLC.
	 */
	public static boolean isCodeLastByteEquals(String cameraCode, byte plcLastByteEncryptedCode) {
		byte[] codeBytes = cameraCode.getBytes();

		return (codeBytes[codeBytes.length - 1]) == plcLastByteEncryptedCode;
	}

	public static boolean isProductStatusEquals(PlcCameraProductStatus plcStatus, ProductStatus cameraStatus) {
		return statusesMapping.get(plcStatus).equals(cameraStatus);
	}


}
