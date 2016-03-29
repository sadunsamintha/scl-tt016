package com.sicpa.tt018.scl.view;

import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.SelectableItem;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.flow.DefaultSelectionFlowView;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.oneSelection.AbstractSelectionView;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.oneSelection.DefaultSmallSelectionView;

public class AlbaniaSelectionFlowView extends DefaultSelectionFlowView {

	private static final long serialVersionUID = 8262181280143342684L;

	protected AbstractSelectionView createView(final SelectableItem[] items) {
		return new DefaultSmallSelectionView(items);
	}

}
