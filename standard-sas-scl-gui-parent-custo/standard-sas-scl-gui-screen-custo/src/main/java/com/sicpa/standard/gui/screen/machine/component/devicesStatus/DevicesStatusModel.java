package com.sicpa.standard.gui.screen.machine.component.devicesStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.EventListenerList;

public class DevicesStatusModel {

	// this allow to keep the order of the device else using statuses.keySet() return a different order
	private List<String> keys;
	private Map<String, DeviceStatus> statuses;
	private Map<String, String> labels;
	protected EventListenerList listenerList;

	public DevicesStatusModel() {
		this.keys = new ArrayList<String>();
		this.listenerList = new EventListenerList();
		this.statuses = new HashMap<String, DeviceStatus>();
		this.labels = new HashMap<String, String>();
	}

	public void addDevice(final String key, final String label) {
		if (this.keys.contains(key)) {
			throw new IllegalArgumentException(key + " already exist");
		}
		this.statuses.put(key, DeviceStatus.UNKNOWN);
		this.labels.put(key, label);
		this.keys.add(key);
		fireDeviceAdded(key, label);
	}

	public void removeDevice(final String key) {
		if (this.labels != null) {
			this.labels.remove(key);
		}
		if (this.statuses != null) {
			this.statuses.remove(key);
		}
		fireStatusRemoved(key);
	}

	public void changeStatus(final String key, final DeviceStatus status) {
		if (this.statuses.get(key) == null) {
			throw new IllegalArgumentException("invalid key:" + key);
		}
		this.statuses.put(key, status);
		fireStatusChanged(key, this.labels.get(key), this.statuses.get(key), status);
	}

	public void addDeviceStatusChangeListener(final DeviceStatusChangeListener listener) {
		this.listenerList.add(DeviceStatusChangeListener.class, listener);
	}

	public void removeDeviceStatusChangeListener(final DeviceStatusChangeListener listener) {
		this.listenerList.remove(DeviceStatusChangeListener.class, listener);
	}

	protected void fireStatusChanged(final String key, final String label, final DeviceStatus oldStatus,
			final DeviceStatus newStatus) {
		Object[] listeners = this.listenerList.getListenerList();
		DeviceStatusChangeEvent e = null;
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == DeviceStatusChangeListener.class) {
				if (e == null) {
					e = new DeviceStatusChangeEvent(key, label, oldStatus, newStatus);
				}
				((DeviceStatusChangeListener) listeners[i + 1]).statusChanged(e);
			}
		}
	}

	protected void fireStatusRemoved(final String key) {
		Object[] listeners = this.listenerList.getListenerList();
		DeviceStatusChangeEvent e = null;
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == DeviceStatusChangeListener.class) {
				if (e == null) {
					e = new DeviceStatusChangeEvent(key, null, null, null);
				}
				((DeviceStatusChangeListener) listeners[i + 1]).deviceRemoved(e);
			}
		}
	}

	protected void fireDeviceAdded(final String key, final String label) {
		Object[] listeners = this.listenerList.getListenerList();
		DeviceStatusChangeEvent e = null;
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == DeviceStatusChangeListener.class) {
				if (e == null) {
					e = new DeviceStatusChangeEvent(key, label, DeviceStatus.UNKNOWN, DeviceStatus.UNKNOWN);
				}
				((DeviceStatusChangeListener) listeners[i + 1]).deviceAdded(e);
			}
		}
	}

	public Collection<String> getKeys() {
		return this.keys;
	}

	public String getLabel(final String key) {
		return this.labels.get(key);
	}

	public DeviceStatus getStatus(final String key) {
		return this.statuses.get(key);
	}

}
