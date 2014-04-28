package com.sicpa.standard.sasscl.devices.camera;

public interface ICameraListener {

	void receiveCameraCode(CameraGoodCodeEvent cameraGoodCodeEvent);

	void receiveCameraCodeError(CameraBadCodeEvent cameraBadCodeEvent);

}
