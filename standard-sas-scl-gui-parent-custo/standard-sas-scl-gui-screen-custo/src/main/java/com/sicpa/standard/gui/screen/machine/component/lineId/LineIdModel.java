package com.sicpa.standard.gui.screen.machine.component.lineId;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.event.EventListenerList;

public class LineIdModel {

	private String lineId = "LINE ID HERE";
	protected EventListenerList listeners;

	public LineIdModel() {
		this.listeners = new EventListenerList();
	}

	public void addLineIdChangeListener(final PropertyChangeListener listener) {
		this.listeners.add(PropertyChangeListener.class, listener);
	}

	public void removeLineIdChangeListener(final PropertyChangeListener listener) {
		this.listeners.remove(PropertyChangeListener.class, listener);
	}

	public void setLineID(final String lineId) {
		if (this.lineId != lineId) {
			this.lineId = lineId;
			fireLineIdChanged(lineId);
		}
	}

	public String getLineId() {
		return this.lineId;
	}

	protected void fireLineIdChanged(final String lineId) {
		Object[] listeners = this.listeners.getListenerList();
		PropertyChangeEvent e = null;
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == PropertyChangeListener.class) {
				if (e == null) {
					e = new PropertyChangeEvent(this, "lineId", null, lineId);
				}
				((PropertyChangeListener) listeners[i + 1]).propertyChange(e);
			}
		}
	}
}
