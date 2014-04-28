/**
 * Author	: YYang
 * Date		: Jan 26, 2011
 *
 * Copyright (c) 2011 SICPA Security Solutions, all rights reserved.
 *
 */
package com.sicpa.standard.sasscl.devices.camera.jobconfig;

/**
 * Class to bind product parameter id and camera job file descriptor
 */
public class ProductCameraJob {

	/**
	 * id of the product parameters
	 * 
	 * e.g. production mode id, SKU id etc
	 */
	protected Integer productId;

	/**
	 * camera job file descriptor of the product parameter
	 */
	protected CameraJobFileDescriptor cameraJobFile;


	public ProductCameraJob(final Integer productId, final CameraJobFileDescriptor cameraJobFile) {
		this.productId = productId;
		this.cameraJobFile = cameraJobFile;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(final Integer productId) {
		this.productId = productId;
	}

	public CameraJobFileDescriptor getCameraJobFile() {
		return cameraJobFile;
	}

	public void setCameraJobFile(final CameraJobFileDescriptor cameraJobFile) {
		this.cameraJobFile = cameraJobFile;
	}
}
