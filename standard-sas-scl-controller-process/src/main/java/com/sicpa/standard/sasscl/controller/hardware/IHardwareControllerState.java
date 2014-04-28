package com.sicpa.standard.sasscl.controller.hardware;

import java.util.List;

import com.sicpa.standard.sasscl.controller.hardware.state.IHardwareControllerStateSetter;
import com.sicpa.standard.sasscl.devices.DeviceStatusEvent;
import com.sicpa.standard.sasscl.devices.IStartableDevice;
import com.sicpa.standard.sasscl.devices.plc.IPlcAdaptor;

public interface IHardwareControllerState {

	/**
	 * connect all devices
	 */
	void connect();

	/**
	 * start all devices then plc
	 */
	void start();

	/**
	 * stop plc them all devices
	 */
	void stop();

	/**
	 * disconnect all devices
	 */
	void disconnect();

	String getName();

	void enter();

	void leave();

	void setSetter(IHardwareControllerStateSetter setter);

	void setPlc(IPlcAdaptor plcAdaptor);

	void setStartableDevices(List<IStartableDevice> startableDevices);

	void deviceStatusChanged(DeviceStatusEvent evt);

	void errorMessageAdded();

	void errorMessageRemoved();
	
	void setDeviceErrorRepository(IDeviceErrorRepository devicesErrors);


}
