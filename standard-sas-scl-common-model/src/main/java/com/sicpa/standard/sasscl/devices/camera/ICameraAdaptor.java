package com.sicpa.standard.sasscl.devices.camera;

import com.sicpa.standard.sasscl.devices.IStartableDevice;

public interface ICameraAdaptor extends IStartableDevice {

	/**
	 * Request camera to start reading camera result
	 * 
	 * @throws CameraAdaptorException
	 */
	@Override
	void start() throws CameraAdaptorException;

	/**
	 * Request camera to stop reading result
	 * 
	 * @throws CameraAdaptorException
	 */
	@Override
	void stop() throws CameraAdaptorException;

	String readParameter(String paramName) throws CameraAdaptorException;

	void writeParameter(String paramName, String value) throws CameraAdaptorException;
	
	void addCameraListener(final ICameraListener listener);

	void removeCameraListener(final ICameraListener listener);
}
