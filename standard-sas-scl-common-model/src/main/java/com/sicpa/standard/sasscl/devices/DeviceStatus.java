package com.sicpa.standard.sasscl.devices;

public class DeviceStatus {

	public final static DeviceStatus CONNECTED = new DeviceStatus(true, "CONNECTED");
	public final static DeviceStatus STARTED = new DeviceStatus(true, "STARTED");
	public final static DeviceStatus STOPPED = new DeviceStatus(true, "STOPPED");
	public final static DeviceStatus DISCONNECTED = new DeviceStatus(false, "DISCONNECTED");
	public final static DeviceStatus UNKNOWN = new DeviceStatus(false, "UNKNOWN");
	public final static DeviceStatus CONNECTING = new DeviceStatus(false, "CONNECTING");
	public final static DeviceStatus DISCONNECTING = new DeviceStatus(false, "DISCONNECTING");

	protected boolean connected;
	protected String description;

	protected DeviceStatus(final boolean connected, final String description) {
		this.connected = connected;
		this.description = description;
	}

	public boolean isConnected() {
		return this.connected;
	}

	@Override
	public String toString() {
		return "device status:" + this.description;
	}

}
