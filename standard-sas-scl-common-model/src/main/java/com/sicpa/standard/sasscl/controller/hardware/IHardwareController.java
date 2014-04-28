package com.sicpa.standard.sasscl.controller.hardware;

import java.util.Collection;

import com.sicpa.standard.sasscl.devices.IStartableDevice;

public interface IHardwareController {

	void connect();

	void disconnect();

	void start();

	void stop();

	void setDevices(Collection<IStartableDevice> devices);

	Collection<IStartableDevice> getDevices();

}
