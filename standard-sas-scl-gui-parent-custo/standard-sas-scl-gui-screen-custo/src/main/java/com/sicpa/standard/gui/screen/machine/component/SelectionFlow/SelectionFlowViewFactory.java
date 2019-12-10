package com.sicpa.standard.gui.screen.machine.component.SelectionFlow;

import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.oneSelection.AbstractSelectionView;

public interface SelectionFlowViewFactory {

	AbstractSelectionView createSelectionView(final SelectableItem[] items);
}
