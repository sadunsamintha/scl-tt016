package com.sicpa.standard.sasscl.controller.productionconfig.config;

/**
 * config of camera related to the current selected production parameter, part of the production config
 * 
 * @author DIelsch
 * 
 */
public class D900CameraConfig extends AbstractLayoutConfig {

	protected D900CameraType cameraType;

	public D900CameraConfig(String id, D900CameraType type) {
		super(id);
		this.cameraType = type;
	}

	public D900CameraConfig() {
	}

	public void setCameraType(D900CameraType cameraType) {
		this.cameraType = cameraType;
	}

	public D900CameraType getCameraType() {
		return cameraType;
	}

}
