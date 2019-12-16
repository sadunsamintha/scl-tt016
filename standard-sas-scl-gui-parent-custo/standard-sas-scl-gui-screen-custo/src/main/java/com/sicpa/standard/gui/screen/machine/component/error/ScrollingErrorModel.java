package com.sicpa.standard.gui.screen.machine.component.error;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.EventListenerList;
@Deprecated
public class ScrollingErrorModel {

	private List<ErrorItem> items;
	protected EventListenerList listeners;

	public ScrollingErrorModel() {
		this.listeners = new EventListenerList();
		this.items = new ArrayList<ErrorItem>();
	}

	public void addError(final ErrorItem item) {
		this.items.add(item);
		fireScrollingErrorAdded(item);
	}

	public void addOrUpdateError(final ErrorItem item) {
		if (!this.items.contains(item)) {
			addError(item);
		}
	}

	public void removeError(final String key) {

		int i = 0;
		while (i < this.items.size()) {
			if (this.items.get(i).getKey().equals(key)) {
				ErrorItem item = this.items.get(i);
				this.items.remove(item);
				fireScrollingErrorRemoved(item);
			} else {
				i++;
			}
		}
	}

	public void addScrolingErrorListener(final ScrollingErrorlistener listener) {
		this.listeners.add(ScrollingErrorlistener.class, listener);
	}

	public void removeSCrolingErrorListener(final ScrollingErrorlistener listener) {
		this.listeners.add(ScrollingErrorlistener.class, listener);
	}

	protected void fireScrollingErrorAdded(final ErrorItem item) {
		Object[] listeners = this.listeners.getListenerList();
		ScrollingErrorEvent e = null;
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ScrollingErrorlistener.class) {
				// Lazily create the event:
				if (e == null) {

					e = new ScrollingErrorEvent(item);
				}
				((ScrollingErrorlistener) listeners[i + 1]).errorAdded(e);
			}
		}
	}

	protected void fireScrollingErrorRemoved(final ErrorItem item) {
		Object[] listeners = this.listeners.getListenerList();
		ScrollingErrorEvent e = null;
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ScrollingErrorlistener.class) {
				// Lazily create the event:
				if (e == null) {

					e = new ScrollingErrorEvent(item);
				}
				((ScrollingErrorlistener) listeners[i + 1]).errorRemoved(e);
			}
		}
	}

	public List<ErrorItem> getItems() {
		return this.items;
	}

	public void reset() {
		while (!this.items.isEmpty()) {
			ErrorItem item = this.items.get(0);
			this.items.remove(0);
			fireScrollingErrorRemoved(item);
		}
	}
}
