package com.sicpa.standard.sasscl.devices;

public class DeviceStatusEvent {

	protected DeviceStatus status;
	protected IDevice device;

	public DeviceStatusEvent(final DeviceStatus status, final IDevice device) {
		super();
		this.status = status;
		this.device = device;
	}

	public DeviceStatus getStatus() {
		return this.status;
	}

	public IDevice getDevice() {
		return this.device;
	}
}
