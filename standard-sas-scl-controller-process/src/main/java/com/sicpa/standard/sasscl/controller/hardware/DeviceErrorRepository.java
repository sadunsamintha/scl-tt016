package com.sicpa.standard.sasscl.controller.hardware;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.HashMultimap;

public class DeviceErrorRepository implements IDeviceErrorRepository {

	private static final Logger logger = LoggerFactory.getLogger(DeviceErrorRepository.class);

	protected final HashMultimap<String, String> errorsKeys = HashMultimap.create();

	// contains the messages to display for each error
	protected final Map<String, String> mapMessages = new HashMap<String, String>();

	public DeviceErrorRepository() {

	}

	public boolean addError(String deviceId, String errorCode, String errorToDisplay) {
		synchronized (errorsKeys) {

			if (!errorsKeys.containsEntry(deviceId, errorCode)) {
				errorsKeys.put(deviceId, errorCode);
				mapMessages.put(deviceId + ":" + errorCode, deviceId + ":" + errorToDisplay);
				logger.error("adding error to repository {} {}", deviceId, errorCode);
				return true;
			} else {
				if (!mapMessages.containsValue(deviceId + ":" + errorToDisplay)) {
					// the error was already there but a parameter changed
					mapMessages.put(deviceId + ":" + errorCode, deviceId + ":" + errorToDisplay);
					return true;
				}
			}
			return false;
		}
	}

	public void removeError(String deviceId, String errorCode) {
		synchronized (errorsKeys) {
			if (errorsKeys.remove(deviceId, errorCode)) {
				mapMessages.remove(deviceId + ":" + errorCode);
				logger.info("removing error from repository {} {}", deviceId, errorCode);
			}
		}
	}

	public boolean isEmpty() {
		synchronized (errorsKeys) {
			return errorsKeys.isEmpty();
		}
	}

	@Override
	public Collection<String> getErrors() {
		synchronized (errorsKeys) {
			return Collections.unmodifiableCollection(mapMessages.values());
		}
	}

	@Override
	public Collection<String> getErrors(String deviceName) {
		synchronized (errorsKeys) {
			return Collections.unmodifiableCollection(errorsKeys.get(deviceName));
		}
	}

	@Override
	public void reset() {
		synchronized (errorsKeys) {
			errorsKeys.clear();
			mapMessages.clear();
		}
	}
}
