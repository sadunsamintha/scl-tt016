package com.sicpa.standard.gui.screen.machine.component.emergency;

import java.beans.PropertyChangeEvent;

import javax.swing.event.EventListenerList;

@Deprecated
public class EmergencyModel {

	private String text;
	private boolean visible;

	protected EventListenerList listeners;

	public EmergencyModel() {
		this.listeners = new EventListenerList();

	}

	public void setText(final String text) {
		this.text = text;
		fireTextChanged(text);
	}

	public void setVisible(final boolean visible) {
		this.visible = visible;
		fireVisibilityChanged(visible);
	}

	public void addEmergencyChangeListener(final EmergencyChangeListener listener) {
		this.listeners.add(EmergencyChangeListener.class, listener);
	}

	public void removeEmergencyChangeListener(final EmergencyChangeListener listener) {
		this.listeners.remove(EmergencyChangeListener.class, listener);
	}

	protected void fireTextChanged(final String text) {
		Object[] listeners = this.listeners.getListenerList();
		PropertyChangeEvent e = null;
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == EmergencyChangeListener.class) {
				// Lazily create the event:
				if (e == null) {
					e = new PropertyChangeEvent(this, "text", null, text);
				}
				((EmergencyChangeListener) listeners[i + 1]).emergencyTextChanged(e);
			}
		}
	}

	protected void fireVisibilityChanged(final boolean visible) {
		Object[] listeners = this.listeners.getListenerList();
		PropertyChangeEvent e = null;
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == EmergencyChangeListener.class) {
				// Lazily create the event:
				if (e == null) {
					e = new PropertyChangeEvent(this, "visibility", null, visible);
				}
				((EmergencyChangeListener) listeners[i + 1]).emergencyVisibilityChanged(e);
			}
		}
	}

	public String getText() {
		return this.text;
	}

	public boolean isVisible() {
		return this.visible;
	}
}
