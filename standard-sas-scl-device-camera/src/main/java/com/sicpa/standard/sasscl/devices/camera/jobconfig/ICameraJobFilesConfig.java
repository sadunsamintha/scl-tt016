package com.sicpa.standard.sasscl.devices.camera.jobconfig;

import java.util.List;

public interface ICameraJobFilesConfig {

	boolean isUseDefaultCameraJobFile();

	CameraJobFileDescriptor getDefaultCameraJobFile();

	CameraJobFileDescriptor retrieveCameraJobConfiguration(List<Integer> productDescriptorIDs);

	boolean isSetCameraJob();

}
