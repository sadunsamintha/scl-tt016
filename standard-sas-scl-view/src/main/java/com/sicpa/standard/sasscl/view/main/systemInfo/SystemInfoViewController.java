package com.sicpa.standard.sasscl.view.main.systemInfo;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.devices.DeviceStatusEvent;
import com.sicpa.standard.sasscl.devices.IDeviceStatusListener;
import com.sicpa.standard.sasscl.devices.plc.IPlcAdaptor;
import com.sicpa.standard.sasscl.devices.remote.IRemoteServer;
import com.sicpa.standard.sasscl.event.ApplicationVersionEvent;
import com.sicpa.standard.sasscl.provider.impl.PlcProvider;

public class SystemInfoViewController implements ISystemInfoViewListener {

	protected SystemInfoModel model;

	public SystemInfoViewController() {
		this(new SystemInfoModel());
	}

	public SystemInfoViewController(SystemInfoModel model) {
		this.model = model;
	}

	public void setRemoteServer(IRemoteServer remoteServer) {
		remoteServer.addDeviceStatusListener(new IDeviceStatusListener() {
			@Override
			public void deviceStatusChanged(DeviceStatusEvent evt) {
				remoteServerStatusChanged(evt);
			}
		});
	}

	protected void remoteServerStatusChanged(DeviceStatusEvent evt) {
		if (evt.getStatus().equals(DeviceStatus.CONNECTED)) {
			model.setRemoteServerConnected(true);
			model.notifyModelChanged();
		} else if (evt.getStatus().equals(DeviceStatus.DISCONNECTED)) {
			model.setRemoteServerConnected(false);
			model.notifyModelChanged();
		}
	}

	@Subscribe
	public void handleApplicationVersion(ApplicationVersionEvent evt) {
		model.setAppVersion(evt.getVersion());
		model.notifyModelChanged();
	}

	public SystemInfoModel getModel() {
		return model;
	}

	public void setPlcProvider(final PlcProvider plcProvider) {
		plcProvider.addChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				plcProvider.get().addDeviceStatusListener(new IDeviceStatusListener() {
					@Override
					public void deviceStatusChanged(DeviceStatusEvent evt) {
						if (evt.getStatus().equals(DeviceStatus.CONNECTED)) {
							plcConnected(plcProvider.get());
						}
					}
				});
			}
		});
	}

	protected void plcConnected(IPlcAdaptor plc) {
		model.setPlcVersion(plc.getPlcVersion());
		model.notifyModelChanged();
	}
}
