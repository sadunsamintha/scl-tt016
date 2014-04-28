package com.sicpa.standard.sasscl.repository.errors;

import java.util.Collection;
import java.util.Map;

import com.sicpa.standard.sasscl.devices.DeviceStatus;

/**
 * 
 * provide status / errors / warnings of devices and application
 * 
 */
public interface IErrorsRepository {

	/**
	 * 
	 * return the device status by using passed in device type and device id
	 * 
	 * @param deviceId
	 *            --> id of the device
	 * 
	 */
	DeviceStatus getDeviceStatus(String deviceId);

	/**
	 * return status of remote server
	 * 
	 */
	DeviceStatus getRemoteServerStatus();

	DeviceStatus getPlcStatus();

	/**
	 * 
	 * return the errors of a given device type and device id
	 * 
	 * @param deviceType
	 *            device type - Camera, Printer, Plc
	 * @param deviceId
	 * @return
	 */
	Collection<String> getErrors(String deviceId);

	/**
	 * 
	 * return a list of errors occurred in application
	 */
	Collection<AppMessage> getApplicationErrors();

	/**
	 * return a list of warning occurred in application
	 */
	Collection<AppMessage> getApplicationWarnings();

	/**
	 * 
	 * @return map < DeviceName , deviceStatus >
	 */
	Map<String, DeviceStatus> getDevicesStatus();

}
