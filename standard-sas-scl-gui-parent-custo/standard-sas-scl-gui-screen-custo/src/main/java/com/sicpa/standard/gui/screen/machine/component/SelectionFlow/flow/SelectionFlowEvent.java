package com.sicpa.standard.gui.screen.machine.component.SelectionFlow.flow;

import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.SelectableItem;

public class SelectionFlowEvent {

	private SelectableItem[] items;
	private String title;
	private boolean movingNext;
	private boolean oldSelection;

	public SelectionFlowEvent(final SelectableItem[] items, final String title, final boolean movingNext) {
		this(items, title, movingNext, false);
	}

	public SelectionFlowEvent(final SelectableItem[] items, final String title, final boolean movingNext,
			final boolean oldSelection) {
		this.items = items;
		this.title = title;
		this.movingNext = movingNext;
		this.oldSelection = oldSelection;
	}

	public SelectableItem[] getItems() {
		return this.items;
	}

	public String getTitle() {
		return this.title;
	}

	public boolean isMovingNext() {
		return this.movingNext;
	}

	public boolean isOldSelection() {
		return this.oldSelection;
	}
}
