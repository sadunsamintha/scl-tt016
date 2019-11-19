package com.sicpa.standard.gui.screen.machine.component.configPassword;

import javax.swing.event.EventListenerList;

@Deprecated
public class ConfigPasswordModel {

	private IConfigPasswordChecker checker;

	protected EventListenerList listeners;

	public ConfigPasswordModel() {
		this.listeners = new EventListenerList();
	}

	public void cancel() {
		fireCancel();
	};

	public void checkPassword(final String password) {
		if (this.checker != null) {
			boolean valid = this.checker.checkPassword(password);
			firePasswordChecked(valid);
		}
	};

	public void addConfigPasswordListener(final ConfigPasswordListener listener) {
		this.listeners.add(ConfigPasswordListener.class, listener);
	}

	public void removeConfigPasswordListener(final ConfigPasswordListener listener) {
		this.listeners.remove(ConfigPasswordListener.class, listener);
	}

	protected void firePasswordChecked(final boolean valid) {
		Object[] listeners = this.listeners.getListenerList();
		ConfigPasswordEvent e = null;
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ConfigPasswordListener.class) {
				// Lazily create the event:
				if (e == null) {
					e = new ConfigPasswordEvent(valid);
				}
				((ConfigPasswordListener) listeners[i + 1]).passwordChecked(e);
			}
		}
	}

	protected void fireCancel() {
		Object[] listeners = this.listeners.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ConfigPasswordListener.class) {

				((ConfigPasswordListener) listeners[i + 1]).canceled();
			}
		}
	}

	public void setChecker(final IConfigPasswordChecker checker) {
		this.checker = checker;
	}
}
