package com.sicpa.standard.gui.screen.machine.component.selectionSummary;

import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.SelectableItem;

public class SummaryEvent {

	private SelectableItem[] summary;

	public SummaryEvent(final SelectableItem[] summary) {
		this.summary = summary;
	}

	public SelectableItem[] getSummary() {
		return this.summary;
	}

}
