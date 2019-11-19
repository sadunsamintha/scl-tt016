package com.sicpa.standard.gui.screen.machine.component.SelectionFlow;

public class SelectionEvent {
	private SelectableItem item;

	public SelectionEvent(final SelectableItem item) {
		this.item = item;
	}

	public SelectableItem getItem() {
		return this.item;
	}

}
