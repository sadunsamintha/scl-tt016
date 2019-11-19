package com.sicpa.standard.gui.screen.machine.component.progress;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.EventListenerList;

public class ProgressModel {

	protected EventListenerList listeners;

	private List<Runnable> runningsTask;

	public ProgressModel() {
		this.listeners = new EventListenerList();
		this.runningsTask = new ArrayList<Runnable>();
	}

	protected void fireRunning(final boolean flag) {
		Object[] listeners = this.listeners.getListenerList();
		PropertyChangeEvent e = null;
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == PropertyChangeListener.class) {
				if (e == null) {
					e = new PropertyChangeEvent(this, "running", null, flag);
				}
				((PropertyChangeListener) listeners[i + 1]).propertyChange(e);
			}
		}
	}

	public void executeInBackground(final Runnable task) {
		this.runningsTask.add(task);
		new Thread(new Runnable() {
			@Override
			public void run() {
				fireRunning(isRunning());
				try {
					task.run();
				} finally {
					ProgressModel.this.runningsTask.remove(task);
					fireRunning(isRunning());
				}
			}
		}).start();
	}

	public boolean isRunning() {
		return !this.runningsTask.isEmpty();
	}

	public void addPropertyChangeListener(final PropertyChangeListener listener) {
		this.listeners.add(PropertyChangeListener.class, listener);
	}

	public void removePropertyChangeListener(final PropertyChangeListener listener) {
		this.listeners.remove(PropertyChangeListener.class, listener);
	}
}
