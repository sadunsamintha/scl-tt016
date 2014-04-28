package com.sicpa.standard.sasscl.devices.camera;

import java.awt.Image;

public class CameraImageEvent {

	protected ICameraAdaptor source;
	protected Image cameraImage;

	public CameraImageEvent(Image cameraImage, ICameraAdaptor source) {
		this.source = source;
		this.cameraImage = cameraImage;
	}

	public Image getCameraImage() {
		return cameraImage;
	}

	public ICameraAdaptor getSource() {
		return source;
	}
}
