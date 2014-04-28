package com.sicpa.standard.sasscl.devices;

/**
 * This interface should be implemented by classes listening to events produced by devices controllers with device status.
 * 
 */
public interface IDeviceStatusListener {
	
	/**
	 * This method is called when a <code>DeviceStatusEvent</code> is sent. This event is produced when a
	 * device status is changed. The object implementing this interface
	 * has to register with the device controller as a listener.
	 * 
	 * @param evt
	 *            is the event produced.
	 */
	void deviceStatusChanged(DeviceStatusEvent evt);
}
