package com.sicpa.standard.sasscl.controller.device;

import com.sicpa.standard.sasscl.controller.device.group.DevicesGroup;
import com.sicpa.standard.sasscl.controller.device.group.IGroupDevicesController;

/**
 * This class should be implemented by the classes that control other devices that is not directly dependent on PLC
 * example remote server, barcode reader where it can be started on demand by process controller
 * 
 */
public interface IPlcIndependentDevicesController {

	/**
	 * Associate a controller for a group of devices with a group.
	 * 
	 * @param controller
	 *            the group devices controller.
	 * @param group
	 *            is the DevicesGroup associated with the devices controller used to identify the group.
	 */
	void addControllerToGroup(final IGroupDevicesController controller, final DevicesGroup group);

	/**
	 * Start only the devices of a particular group.
	 * 
	 * @param group
	 *            is the group of devices to start.
	 */
	void startDevicesGroup(final DevicesGroup group);

	/**
	 * Stop the devices of a particular group.
	 * 
	 * @param group
	 *            is the group of devices to stop.
	 */
	void stopDevicesGroup(final DevicesGroup group);

	/**
	 * return the controller corresponding to the given group
	 */
	IGroupDevicesController getController(DevicesGroup group);

}
