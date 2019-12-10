package com.sicpa.standard.gui.screen.machine.component.selectionSummary;

import javax.swing.event.EventListenerList;

import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.SelectableItem;

public class SummaryModel {

	private SelectableItem[] summary;

	protected EventListenerList listeners;

	public SummaryModel() {
		this.listeners = new EventListenerList();
	}

	public void addSummaryListener(final SummaryListener listener) {
		this.listeners.add(SummaryListener.class, listener);
	}

	public void removeSummaryListener(final SummaryListener listener) {
		this.listeners.remove(SummaryListener.class, listener);
	}

	public SelectableItem[] getSummary() {
		return this.summary;
	}

	public void setSummary(final SelectableItem[] summary) {
		this.summary = summary;
		fireSummaryChanged(summary);
	}

	protected void fireSummaryChanged(final SelectableItem[] summary) {
		Object[] listeners = this.listeners.getListenerList();
		SummaryEvent e = null;
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == SummaryListener.class) {
				if (e == null) {
					e = new SummaryEvent(summary);
				}
				((SummaryListener) listeners[i + 1]).SummaryChanged(e);
			}
		}
	}
}
