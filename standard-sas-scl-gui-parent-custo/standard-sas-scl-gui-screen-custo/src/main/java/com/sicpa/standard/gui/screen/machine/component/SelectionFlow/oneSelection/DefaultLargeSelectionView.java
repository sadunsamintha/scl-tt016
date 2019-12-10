package com.sicpa.standard.gui.screen.machine.component.SelectionFlow.oneSelection;

import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.SelectableItem;

public class DefaultLargeSelectionView extends AbstractSelectionButtonView {

	public DefaultLargeSelectionView(final SelectableItem[] items) {
		super(2, items);
	}

	public DefaultLargeSelectionView() {
	}

	@Override
	protected int getButtonHeight() {
		return 50;
	}

	@Override
	protected float getRatio() {
		return 0.8f;
	}

	@Override
	protected int getMaxButtonWidth() {
		return 200;
	}
}
