package com.sicpa.standard.gui.screen.machine.component.applicationStatus;

import java.beans.PropertyChangeEvent;

import javax.swing.event.EventListenerList;

public class ApplicationStatusModel {

	private ApplicationStatus applicationStatus;
	protected EventListenerList listeners;

	public ApplicationStatusModel() {
		this.listeners = new EventListenerList();
		this.applicationStatus=new ApplicationStatus();
	}

	public ApplicationStatus getApplicationStatus() {
		return this.applicationStatus;
	}

	public void setApplicationStatus(final ApplicationStatus applicationStatus) {
		this.applicationStatus = applicationStatus;
		fireApplicationStatusChanged(applicationStatus);
	}

	protected void fireApplicationStatusChanged(final ApplicationStatus status) {
		Object[] listeners = this.listeners.getListenerList();
		PropertyChangeEvent e = null;
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ApplicationStatusListener.class) {
				// Lazily create the event:
				if (e == null) {
					e = new PropertyChangeEvent(this, "color", null, status);
				}
				((ApplicationStatusListener) listeners[i + 1]).applicationStatusChanged(e);
			}
		}
	}

	public void addApplicationStatusListener(final ApplicationStatusListener listener) {
		this.listeners.add(ApplicationStatusListener.class, listener);
	}

	public void removeApplicationStatusListener(final ApplicationStatusListener listener) {
		this.listeners.remove(ApplicationStatusListener.class, listener);
	}
}
