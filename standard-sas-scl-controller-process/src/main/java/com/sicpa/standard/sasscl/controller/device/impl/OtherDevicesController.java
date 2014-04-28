package com.sicpa.standard.sasscl.controller.device.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.sasscl.controller.device.IPlcIndependentDevicesController;
import com.sicpa.standard.sasscl.controller.device.group.DevicesGroup;
import com.sicpa.standard.sasscl.controller.device.group.IGroupDevicesController;

/**
 * This class provides an implementation of a controller for groups of devices.
 * 
 * In this way, each group of devices can be operated independently by its own controller. It is only necessary to
 * specify the group to start or stop the devices on that a particular group, managed by its associated controller.
 * 
 * @author JPerez
 * 
 */
public class OtherDevicesController implements IPlcIndependentDevicesController {

	private final static Logger logger = LoggerFactory.getLogger(OtherDevicesController.class);

	// Map to associate a DeviceGroup with a particular controller.
	protected Map<DevicesGroup, IGroupDevicesController> controllerList;

	public OtherDevicesController() {
		this.controllerList = new HashMap<DevicesGroup, IGroupDevicesController>();
	}

	public void startDevicesGroup(final DevicesGroup group) {
		logger.debug("Start devices group: {}", group.toString());
		IGroupDevicesController controller = controllerList.get(group);

		if (controller == null) {
			throw new IllegalArgumentException("No controller for:" + group);
		}
		controller.start();
	}

	public void stopDevicesGroup(final DevicesGroup group) {
		logger.debug("Stop devices group: {}", group.toString());
		IGroupDevicesController controller = controllerList.get(group);

		if (controller == null) {
			throw new IllegalArgumentException("No controller for:" + group);
		}
		controller.stop();
	}

	/**
	 * Associate a controller with a group of devices.
	 * 
	 * @param controller
	 *            the group of devices to add.
	 * @param group
	 *            the associated devices group
	 */
	public void addControllerToGroup(final IGroupDevicesController controller, final DevicesGroup group) {
		controllerList.put(group, controller);
	}

	/**
	 * Associate a controller with a group of devices.
	 * 
	 * @param controller
	 *            for the devices.
	 * @param group
	 *            the name of the type of devices group to which the controller is associated. Using this method a new
	 *            DeviceGroup (not predefined) is created and associated with the controller. Note that the DeviceGroup
	 *            comparison is case sensitive.
	 */
	public void addControllerToGroup(final IGroupDevicesController controller, final String group) {
		addControllerToGroup(controller, new DevicesGroup(group));
	}

	@Override
	public IGroupDevicesController getController(DevicesGroup group) {
		return controllerList.get(group);
	}

}
