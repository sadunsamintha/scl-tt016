package com.sicpa.standard.sasscl.devices;

public class DeviceStatusEvent {

	private DeviceStatus status;
	private IDevice device;

	public DeviceStatusEvent(DeviceStatus status, IDevice device) {
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
