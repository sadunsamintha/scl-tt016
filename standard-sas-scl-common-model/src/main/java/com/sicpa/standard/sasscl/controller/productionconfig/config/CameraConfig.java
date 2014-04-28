package com.sicpa.standard.sasscl.controller.productionconfig.config;

/**
 * config of camera related to the current selected production parameter, part of the production config
 * 
 * @author DIelsch
 * 
 */
public class CameraConfig extends AbstractLayoutConfig {

	protected CameraType cameraType;

	public CameraConfig(String id, CameraType type) {
		super(id);
		this.cameraType = type;
	}

	public CameraConfig() {
	}

	public void setCameraType(CameraType cameraType) {
		this.cameraType = cameraType;
	}

	public CameraType getCameraType() {
		return cameraType;
	}

}
