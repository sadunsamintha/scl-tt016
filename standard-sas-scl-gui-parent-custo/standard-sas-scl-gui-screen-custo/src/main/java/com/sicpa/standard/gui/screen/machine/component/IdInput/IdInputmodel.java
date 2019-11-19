package com.sicpa.standard.gui.screen.machine.component.IdInput;

import javax.swing.event.EventListenerList;

public class IdInputmodel {

	private String id;
	private String description;
	private String error;

	protected EventListenerList listeners;

	public IdInputmodel() {
		this.listeners = new EventListenerList();
	}

	public void addIdListener(final IdInputListener listener) {
		this.listeners.add(IdInputListener.class, listener);
	}

	public void removeIdListener(final IdInputListener listener) {
		this.listeners.remove(IdInputListener.class, listener);
	}

	public void setId(final String id) {
		this.id = id;
		fireIdChanged();
	}

	public String getId() {
		return this.id;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
		fireDescriptionChanged();
	}

	protected void fireIdChanged() {
		Object[] listeners = this.listeners.getListenerList();
		IdInputEvent e = null;
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == IdInputListener.class) {
				if (e == null) {
					e = new IdInputEvent(this.description, this.id);
				}
				((IdInputListener) listeners[i + 1]).idChanged(e);
			}
		}
	}

	protected void fireDescriptionChanged() {
		Object[] listeners = this.listeners.getListenerList();
		IdInputEvent e = null;
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == IdInputListener.class) {
				if (e == null) {
					e = new IdInputEvent(this.description, this.id);
				}
				((IdInputListener) listeners[i + 1]).descriptionChanged(e);
			}
		}
	}

	protected void fireComplete() {
		Object[] listeners = this.listeners.getListenerList();
		IdInputEvent e = null;
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == IdInputListener.class) {
				if (e == null) {
					e = new IdInputEvent(this.description, this.id);
				}
				((IdInputListener) listeners[i + 1]).complete(e);
			}
		}
	}

	protected void fireError() {
		Object[] listeners = this.listeners.getListenerList();
		IdInputEvent e = null;
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == IdInputListener.class) {
				if (e == null) {
					e = new IdInputEvent(this.description, this.id, this.error);
				}
				((IdInputListener) listeners[i + 1]).error(e);
			}
		}
	}

	public String getError() {
		return this.error;
	}

	public void setError(final String error) {
		this.error = error;
		fireError();
	}

	public void reset() {
		setError("");
		setId("");
	}

	public void selectionComplete() {
		fireComplete();
	}
}
