package com.sicpa.standard.sasscl.devices.d900;

import com.sicpa.standard.sasscl.devices.IStartableDevice;

public interface ID900CameraAdaptor extends IStartableDevice {

	@Override
	void start() throws D900CameraAdaptorException;

	@Override
	void stop() throws D900CameraAdaptorException;

	String readParameter(String paramName) throws D900CameraAdaptorException;

	void writeParameter(String paramName, String value) throws D900CameraAdaptorException;

}
