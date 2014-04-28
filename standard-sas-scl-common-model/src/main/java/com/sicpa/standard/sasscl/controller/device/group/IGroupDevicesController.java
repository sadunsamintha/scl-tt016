package com.sicpa.standard.sasscl.controller.device.group;

import java.util.Collection;

import com.sicpa.standard.sasscl.devices.IDevice;

/**
 * This class provides a skeleton for a controller of a group of devices.
 * 
 */
public interface IGroupDevicesController {

	/**
	 * Start the devices driven by the controller.
	 */
	public void start();

	/**
	 * Stop the devices driven by the controller.
	 */
	public void stop();

	public Collection<IDevice> getDevices();

}
