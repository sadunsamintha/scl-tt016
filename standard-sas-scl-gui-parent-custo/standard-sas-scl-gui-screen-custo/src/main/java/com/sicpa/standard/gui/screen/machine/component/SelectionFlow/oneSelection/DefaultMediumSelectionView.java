package com.sicpa.standard.gui.screen.machine.component.SelectionFlow.oneSelection;

import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.SelectableItem;

public class DefaultMediumSelectionView extends AbstractSelectionButtonView {

	public DefaultMediumSelectionView(final SelectableItem[] items) {
		super(2, items);
	}

	public DefaultMediumSelectionView() {
	}

	@Override
	protected int getButtonHeight() {
		return 80;
	}

	@Override
	protected int getMaxButtonWidth() {
		return 200;
	}
}
