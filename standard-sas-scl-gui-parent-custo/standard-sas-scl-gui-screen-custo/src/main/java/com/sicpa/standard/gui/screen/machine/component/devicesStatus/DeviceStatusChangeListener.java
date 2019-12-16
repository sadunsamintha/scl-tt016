package com.sicpa.standard.gui.screen.machine.component.devicesStatus;

import java.util.EventListener;

public interface DeviceStatusChangeListener extends EventListener {
	void statusChanged(DeviceStatusChangeEvent evt);

	void deviceAdded(DeviceStatusChangeEvent evt);

	void deviceRemoved(DeviceStatusChangeEvent evt);
}
