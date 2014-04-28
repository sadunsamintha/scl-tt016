package com.sicpa.standard.sasscl.controller.device.impl;

import org.junit.Ignore;

import com.sicpa.standard.sasscl.controller.device.group.IGroupDevicesController;
import com.sicpa.standard.sasscl.devices.AbstractDevice;
import com.sicpa.standard.sasscl.devices.DeviceException;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.devices.IStartableDevice;

@Ignore
public class GroupDevicesControllerBaseHelper {

	protected CameraDevice cameraDevice;

	protected PrinterDevice printerDevice;

	protected PlcDevice plcDevice;

	protected IGroupDevicesController devicesController;

	// Dummy camera device
	class CameraDevice extends AbstractStartableDevice {
		@Override
		public String getName() {
			return "camera";
		}
	}

	protected boolean printerDisconnectedCalled;

	// Dummy printer device
	class PrinterDevice extends AbstractStartableDevice {

		@Override
		public void disconnect() {
			printerDisconnectedCalled = true;
			super.disconnect();
		}

		@Override
		public String getName() {
			return "printer";
		}
	}

	// Dummy plc device
	class PlcDevice extends AbstractStartableDevice {
		@Override
		public String getName() {
			return "plc";
		}
	}

	public abstract class AbstractStartableDevice extends AbstractDevice implements IStartableDevice {

		@Override
		public void start() throws DeviceException {
			if (status == DeviceStatus.CONNECTED) {
				fireDeviceStatusChanged(DeviceStatus.STARTED);
			}
		}

		@Override
		public void stop() throws DeviceException {
			if (status == DeviceStatus.STARTED) {
				fireDeviceStatusChanged(DeviceStatus.STOPPED);
			}
		}

		@Override
		protected void doConnect() throws DeviceException {
			if (status == DeviceStatus.CONNECTING) {
				fireDeviceStatusChanged(DeviceStatus.CONNECTED);
			}
		}

		@Override
		protected void doDisconnect() throws DeviceException {
			if (status == DeviceStatus.DISCONNECTING) {
				fireDeviceStatusChanged(DeviceStatus.DISCONNECTED);
			}
		}

		@Override
		public boolean isBlockProductionStart() {
			return false;
		}
	}

}
