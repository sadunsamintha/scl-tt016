package com.sicpa.standard.sasscl.controller.hardware;

import java.util.Collection;

import com.sicpa.standard.sasscl.devices.IStartableDevice;

public class ProductionDevicesCreatedEvent {

	protected final Collection<IStartableDevice> devices;

	public ProductionDevicesCreatedEvent(Collection<IStartableDevice> devices) {
		this.devices = devices;
	}

	public Collection<IStartableDevice> getDevices() {
		return devices;
	}
}
