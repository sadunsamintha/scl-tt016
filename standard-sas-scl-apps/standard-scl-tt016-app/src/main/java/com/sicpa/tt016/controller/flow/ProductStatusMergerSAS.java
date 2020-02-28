package com.sicpa.tt016.controller.flow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.sasscl.devices.camera.blobDetection.BlobDetectionUtils;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.ProductStatus;
import com.sicpa.tt016.model.PlcCameraProductStatus;
import com.sicpa.tt016.model.PlcCameraResult;

public class ProductStatusMergerSAS extends ProductStatusMerger {

	private static final Logger logger = LoggerFactory.getLogger(ProductStatusMergerSAS.class);

	protected BlobDetectionUtils blobDetectionUtils;
	protected boolean qrCodeActivation;
	
	@Override
	protected void mergeProductStatuses() {
		Product product = getProductFromCameraResult(cameraResults.poll());
		
		// Check if Blob is detected then set Product Status to Ink Detected
		if (!qrCodeActivation && blobDetectionUtils.isBlobDetected(product.getCode())) {
			product.setStatus(ProductStatus.INK_DETECTED);
		}
		
		PlcCameraResult plcCameraResult = plcCameraResults.poll();
		logger.debug("Java App received status cameraStatus:{} , plcStatus:{}",product.getStatus() ,plcCameraResult.getPlcCameraProductStatus());
		PlcCameraProductStatus plcCameraProductStatus = plcCameraResult.getPlcCameraProductStatus();

		if (!isPlcCameraProductStatusNotDefined(plcCameraProductStatus)) {
			if (isPlcCameraProductStatusEjected(plcCameraProductStatus)) {
				setProductAsEjected(product);
			} else if (isPlcCameraProductStatusAcquisitionError(plcCameraProductStatus)) {
				setProductAsUnread(product);
			} else if (!qrCodeActivation && isPlcCameraProductStatusInkDetected(plcCameraProductStatus)) {
				setProductAsInkDetected(product);
			} else if (isPlcCameraProductStatusNoInk(plcCameraProductStatus)) {
				setProductAsEjected(product);
			} else {
				ProductValidator.validate(product, plcCameraResult);
			}
		}

		setProductProperties(product);

		fireNewProduct(product);
	}

	public void setBlobDetectionUtils(BlobDetectionUtils blobDetectionUtils) {
		this.blobDetectionUtils = blobDetectionUtils;
	}

	public void setQrCodeActivation(boolean qrCodeActivation) {
		this.qrCodeActivation = qrCodeActivation;
	}
}