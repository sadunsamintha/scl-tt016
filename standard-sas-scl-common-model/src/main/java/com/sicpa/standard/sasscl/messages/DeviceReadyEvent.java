package com.sicpa.standard.sasscl.messages;

import com.sicpa.standard.sasscl.devices.IDevice;

public class DeviceReadyEvent {
	private IDevice source;

	public DeviceReadyEvent(IDevice source) {
		super();
		this.source = source;
	}

	public IDevice getSource() {
		return source;
	}

}
