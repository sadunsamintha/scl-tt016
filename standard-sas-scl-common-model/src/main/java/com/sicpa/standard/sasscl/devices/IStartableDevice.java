package com.sicpa.standard.sasscl.devices;

public interface IStartableDevice extends IDevice {

	void start() throws DeviceException;

	void stop() throws DeviceException;

	/**
	 * will this device block start when it disconnected?
	 * 
	 * @return
	 */
	boolean isBlockProductionStart();

	void setName(String name);

}
