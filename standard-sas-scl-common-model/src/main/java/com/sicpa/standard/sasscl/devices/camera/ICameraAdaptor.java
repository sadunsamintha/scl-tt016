package com.sicpa.standard.sasscl.devices.camera;

import com.sicpa.standard.sasscl.devices.IStartableDevice;

public interface ICameraAdaptor extends IStartableDevice {

	@Override
	void start() throws CameraAdaptorException;

	@Override
	void stop() throws CameraAdaptorException;

	String readParameter(String paramName) throws CameraAdaptorException;

	void writeParameter(String paramName, String value) throws CameraAdaptorException;

}
