package com.sicpa.standard.gui.screen.machine.component.SelectionFlow.flow;

import java.util.List;

import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.SelectableItem;

public class DefaultSelectionFlowModel extends AbstractSelectionFlowModel {

	private SelectableItem[][] items;
	private String[] titles;

	public DefaultSelectionFlowModel(final SelectableItem[][] items, final String[] titles) {
		this.items = items;
		this.titles = titles;
	}

	@Override
	public String getTitle(final SelectableItem[] selection) {
		return this.titles[selection.length];
	}

	@Override
	public boolean isLeaf(final SelectableItem[] selection) {
		return this.items.length == selection.length;
	}

	@Override
	protected void populate(final SelectableItem[] selections, final List<SelectableItem> nextOptions) {
		for (SelectableItem item : this.items[selections.length]) {
			nextOptions.add(item);
		}
	}
}
