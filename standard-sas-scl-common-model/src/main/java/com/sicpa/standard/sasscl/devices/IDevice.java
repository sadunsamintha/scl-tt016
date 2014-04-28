package com.sicpa.standard.sasscl.devices;

public interface IDevice {

	/**
	 * Connect to the device.
	 */
	void connect() throws DeviceException;

	/**
	 * Disconnect from the device.
	 */
	void disconnect();

	/**
	 * Is this device connected?
	 * 
	 * @return true if connected, false otherwise.
	 */
	boolean isConnected();

	/**
	 * Get the status of device
	 * 
	 * @return
	 */
	DeviceStatus getStatus();

	void addDeviceStatusListener(final IDeviceStatusListener listener);

	void removeDeviceStatusListener(final IDeviceStatusListener listener);

	/**
	 * Get name of device
	 * 
	 * @return
	 */
	String getName();

}
