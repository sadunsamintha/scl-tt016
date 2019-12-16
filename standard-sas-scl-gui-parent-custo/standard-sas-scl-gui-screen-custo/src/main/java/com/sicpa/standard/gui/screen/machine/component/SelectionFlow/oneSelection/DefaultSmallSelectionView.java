package com.sicpa.standard.gui.screen.machine.component.SelectionFlow.oneSelection;

import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.SelectableItem;

public class DefaultSmallSelectionView extends AbstractSelectionButtonView {

	public DefaultSmallSelectionView(final SelectableItem[] items) {
		super(1, items);
	}

	public DefaultSmallSelectionView() {
	}

	@Override
	protected int getButtonHeight() {
		return 80;
	}

	@Override
	protected int getMaxButtonWidth() {
		return 400;
	}
}
