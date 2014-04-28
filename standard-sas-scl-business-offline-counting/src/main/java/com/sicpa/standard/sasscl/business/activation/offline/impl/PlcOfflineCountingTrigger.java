package com.sicpa.standard.sasscl.business.activation.offline.impl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.sicpa.standard.sasscl.business.activation.offline.IOfflineCounting;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.devices.DeviceStatusEvent;
import com.sicpa.standard.sasscl.devices.IDeviceStatusListener;
import com.sicpa.standard.sasscl.provider.impl.PlcProvider;

public class PlcOfflineCountingTrigger implements IDeviceStatusListener {

	protected IOfflineCounting offlineCounting;

	public PlcOfflineCountingTrigger(IOfflineCounting offlineCounting) {
		this.offlineCounting = offlineCounting;
	}

	@Override
	public void deviceStatusChanged(DeviceStatusEvent evt) {
		if (evt.getStatus().equals(DeviceStatus.CONNECTED)) {
			offlineCounting.processOfflineCounting();
		}
	}

	public void setPlcProvider(final PlcProvider plcProvider) {
		plcProvider.addChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent arg0) {
				plcProvider.get().addDeviceStatusListener(PlcOfflineCountingTrigger.this);
			}
		});
	}
}
