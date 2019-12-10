package com.sicpa.standard.gui.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.event.EventListenerList;

public class BasicMapLikeModel {

	private Map<String, Object> map;

	protected EventListenerList listeners;
	private ArrayList<String> availableProperties;

	public BasicMapLikeModel(final String... availableProperties) {
		this.listeners = new EventListenerList();
		this.availableProperties = new ArrayList<String>();
		Collections.addAll(this.availableProperties, availableProperties);
		this.map = new HashMap<String, Object>();
	}

	public void addPropertyChangeListener(final PropertyChangeListener listener) {
		this.listeners.add(PropertyChangeListener.class, listener);
	}

	public void removePropertyChangeListener(final PropertyChangeListener listener) {
		this.listeners.remove(PropertyChangeListener.class, listener);
	}

	public void setProperty(final String key, final Object value) {
		if (!this.availableProperties.contains(key)) {
			throw new IllegalArgumentException(key + " is not an available properties" + this.availableProperties);
		}

		Object oldValue = this.map.get(key);
		this.map.put(key, value);
		firePropertyChanged(key, oldValue, value);
	}

	protected void firePropertyChanged(final String key, final Object oldValue, final Object newValue) {
		Object[] listeners = this.listeners.getListenerList();
		PropertyChangeEvent e = null;
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == PropertyChangeListener.class) {
				if (e == null) {
					e = new PropertyChangeEvent(this, key, oldValue, newValue);
				}
				((PropertyChangeListener) listeners[i + 1]).propertyChange(e);
			}
		}
	}

	public Map<String, Object> getAllProperties() {
		return this.map;
	}

	public void addAvailableProperty(final String... props) {
		Collections.addAll(this.availableProperties, props);
	}

	public void removeAvailableProperty(final String prop) {
		this.availableProperties.remove(prop);
	}
}
