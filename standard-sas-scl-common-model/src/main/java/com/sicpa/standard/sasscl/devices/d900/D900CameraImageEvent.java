package com.sicpa.standard.sasscl.devices.d900;

import java.awt.Image;

public class D900CameraImageEvent {

	protected ID900CameraAdaptor source;
	protected Image cameraImage;

	public D900CameraImageEvent(Image cameraImage, ID900CameraAdaptor source) {
		this.source = source;
		this.cameraImage = cameraImage;
	}

	public Image getCameraImage() {
		return cameraImage;
	}

	public ID900CameraAdaptor getSource() {
		return source;
	}
}
