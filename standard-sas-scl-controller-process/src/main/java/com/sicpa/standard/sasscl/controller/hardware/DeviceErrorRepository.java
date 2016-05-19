package com.sicpa.standard.sasscl.controller.hardware;

import static java.util.Collections.unmodifiableCollection;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.HashMultimap;

public class DeviceErrorRepository implements IDeviceErrorRepository {

	private static final Logger logger = LoggerFactory.getLogger(DeviceErrorRepository.class);

	private final HashMultimap<String, String> errorsByDevice = HashMultimap.create();

	// contains the messages to display for each error
	private final Map<String, String> msgToDisplayByError = new HashMap<>();

	public DeviceErrorRepository() {

	}

	@Override
	public boolean addError(String deviceId, String errorCode, String errorToDisplay) {
		synchronized (errorsByDevice) {

			String msgToDisplayKey = formatMsgToDisplayKey(deviceId, errorCode);
			String msgToDisplayValue = formatMsgToDisplayValue(deviceId, errorToDisplay);
			if (!errorsByDevice.containsEntry(deviceId, errorCode)) {
				errorsByDevice.put(deviceId, errorCode);
				msgToDisplayByError.put(msgToDisplayKey, msgToDisplayValue);
				logger.error("adding error to repository {} {}", deviceId, errorCode);
				return true;
			} else {
				if (!msgToDisplayByError.containsValue(msgToDisplayValue)) {
					// the error was already there but a parameter changed
					msgToDisplayByError.put(msgToDisplayKey, msgToDisplayValue);
					return true;
				}
			}
			return false;
		}
	}

	private String formatMsgToDisplayKey(String deviceId, String errorCode) {
		return deviceId + ":" + errorCode;
	}

	private String formatMsgToDisplayValue(String deviceId, String errorToDisplay) {
		return deviceId + ":" + errorToDisplay;
	}

	@Override
	public void removeError(String deviceId, String errorCode) {
		synchronized (errorsByDevice) {
			if (errorsByDevice.remove(deviceId, errorCode)) {
				msgToDisplayByError.remove(deviceId + ":" + errorCode);
				logger.info("removing error from repository {} {}", deviceId, errorCode);
			}
		}
	}

	@Override
	public void removeAllErrors(String deviceId) {
		// TODO add test
		synchronized (errorsByDevice) {
			getErrors(deviceId).forEach(error -> removeError(deviceId, error));
		}
	}

	public boolean isEmpty() {
		synchronized (errorsByDevice) {
			return errorsByDevice.isEmpty();
		}
	}

	@Override
	public Collection<String> getErrors() {
		synchronized (errorsByDevice) {
			return unmodifiableCollection(msgToDisplayByError.values());
		}
	}

	@Override
	public Collection<String> getErrors(String deviceName) {
		synchronized (errorsByDevice) {
			return unmodifiableCollection(errorsByDevice.get(deviceName));
		}
	}

	@Override
	public void reset() {
		synchronized (errorsByDevice) {
			errorsByDevice.clear();
			msgToDisplayByError.clear();
		}
	}
}
