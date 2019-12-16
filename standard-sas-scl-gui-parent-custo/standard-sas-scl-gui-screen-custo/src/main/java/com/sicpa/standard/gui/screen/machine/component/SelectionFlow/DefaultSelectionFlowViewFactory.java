package com.sicpa.standard.gui.screen.machine.component.SelectionFlow;

import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.oneSelection.AbstractSelectionView;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.oneSelection.DefaultLargeSelectionView;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.oneSelection.DefaultMediumSelectionView;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.oneSelection.DefaultSmallSelectionView;

public class DefaultSelectionFlowViewFactory implements SelectionFlowViewFactory {

	public AbstractSelectionView createSelectionView(final SelectableItem[] items) {
		if (items.length > 8) {
			return new DefaultLargeSelectionView(items);
		} else if (items.length > 4) {
			return new DefaultMediumSelectionView(items);
		} else {
			return new DefaultSmallSelectionView(items);
		}
	}
}
