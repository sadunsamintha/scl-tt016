package com.sicpa.standard.sasscl.controller.device.group.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.utils.TaskExecutor;
import com.sicpa.standard.sasscl.controller.device.group.IGroupDevicesController;
import com.sicpa.standard.sasscl.devices.DeviceException;
import com.sicpa.standard.sasscl.devices.IDevice;

/**
 * This class provides an implementation of a devices controller where all devices are connected / disconnected at the
 * same time without priority or ordering.
 * 
 * @author JPerez
 * 
 */
public class SimpleGroupDevicesController implements IGroupDevicesController {

	private static final Logger logger = LoggerFactory.getLogger(SimpleGroupDevicesController.class);

	protected final Set<IDevice> devices = new HashSet<IDevice>();

	public SimpleGroupDevicesController() {
	}

	/**
	 * Associate a device with the controller.
	 */
	public void addDevice(final IDevice device) {
		synchronized (devices) {
			devices.add(device);
		}
	}

	public void removeDevice(final IDevice device) {
		synchronized (devices) {
			devices.remove(device);
		}
	}

	@Override
	public void start() {
		logger.debug("Starting simple group devices controller");
		synchronized (devices) {
			for (final IDevice device : devices) {
				TaskExecutor.execute(new Runnable() {
					@Override
					public void run() {
						try {
							device.connect();
						} catch (final DeviceException e) {
							logger.error("Error while starting device", e);
						}
					}
				});
			}
		}
	}

	@Override
	public void stop() {
		logger.debug("Stopping simple group devices controller");

		synchronized (devices) {
			for (final IDevice device : devices) {
				logger.debug("Stopping device: {}", device.getName());
				TaskExecutor.execute(new Runnable() {
					@Override
					public void run() {
						device.disconnect();
					}
				});
			}
		}
	}

	@Override
	public Collection<IDevice> getDevices() {
		return devices;
	}

}
