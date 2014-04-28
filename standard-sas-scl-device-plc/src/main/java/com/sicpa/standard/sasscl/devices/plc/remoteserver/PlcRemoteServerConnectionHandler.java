package com.sicpa.standard.sasscl.devices.plc.remoteserver;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.plc.value.IPlcVariable;
import com.sicpa.standard.plc.value.PlcVariable;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.devices.DeviceStatusEvent;
import com.sicpa.standard.sasscl.devices.IDevice;
import com.sicpa.standard.sasscl.devices.IDeviceStatusListener;
import com.sicpa.standard.sasscl.devices.plc.IPlcAdaptor;
import com.sicpa.standard.sasscl.devices.plc.impl.PlcVariables;
import com.sicpa.standard.sasscl.provider.impl.PlcProvider;

public class PlcRemoteServerConnectionHandler {

	private static final Logger logger = LoggerFactory.getLogger(PlcRemoteServerConnectionHandler.class);

	protected IDevice remoteServer;
	protected PlcProvider plcProvider;

	public void setRemoteServer(IDevice remoteServer) {
		this.remoteServer = remoteServer;
		this.remoteServer.addDeviceStatusListener(remoteServerStatusListener);
	}

	public void setPlcProvider(PlcProvider plcProvider) {
		this.plcProvider = plcProvider;
		plcProvider.addChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent arg0) {
				handleNewPlc();
			}
		});
	}

	protected void handleNewPlc() {
		plcProvider.get().addDeviceStatusListener(plcStatusListener);
	}

	public void plcStatusChanged(DeviceStatusEvent evt) {
		if (evt.getStatus().equals(DeviceStatus.CONNECTED)) {
			notifyRemoteServerDown(!remoteServer.isConnected());
		}
	}

	protected IDeviceStatusListener plcStatusListener = new IDeviceStatusListener() {
		@Override
		public void deviceStatusChanged(DeviceStatusEvent evt) {
			plcStatusChanged(evt);
		}
	};

	protected IDeviceStatusListener remoteServerStatusListener = new IDeviceStatusListener() {
		@Override
		public void deviceStatusChanged(DeviceStatusEvent evt) {
			remoteServerStatusChanged(evt);
		}
	};

	public void remoteServerStatusChanged(DeviceStatusEvent evt) {
		if (evt.getStatus().equals(DeviceStatus.DISCONNECTED)) {
			notifyRemoteServerDown(true);
		} else if (evt.getStatus().equals(DeviceStatus.CONNECTED)) {
			notifyRemoteServerDown(false);
		}
	}

	protected void notifyRemoteServerDown(boolean isDown) {
		try {
			IPlcAdaptor plc = plcProvider.get();
			if (plc == null) {
				return;
			}

			if (!plc.isConnected()) {
				return;
			}

			IPlcVariable<Integer> var = PlcVariable
					.createInt32Var(PlcVariables.REQUEST_JAVA_WARNINGS_AND_ERRORS_REGISTER.getVariableName());

			var.setValue(isDown ? 1 : 0);
			plcProvider.get().write(var);

		} catch (Exception e) {
			logger.error("Failed to update the remote server status variable", e);
		}
	}
}
