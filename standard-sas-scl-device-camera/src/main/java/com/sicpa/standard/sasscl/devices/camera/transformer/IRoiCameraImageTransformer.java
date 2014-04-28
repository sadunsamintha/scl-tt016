package com.sicpa.standard.sasscl.devices.camera.transformer;

import com.sicpa.standard.sasscl.devices.camera.ICameraAdaptor;

public interface IRoiCameraImageTransformer extends ICameraImageTransformer {

	void setCamera(ICameraAdaptor cameraAdaptor);
	
}
