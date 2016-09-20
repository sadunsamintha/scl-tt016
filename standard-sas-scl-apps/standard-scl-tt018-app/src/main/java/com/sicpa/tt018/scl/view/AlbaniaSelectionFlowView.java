package com.sicpa.tt018.scl.view;

import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.SelectableItem;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.flow.DefaultSelectionFlowView;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.oneSelection.AbstractSelectionView;
import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.oneSelection.DefaultSmallSelectionView;
import com.sicpa.standard.sasscl.view.selection.select.productionparameters.ProductionParametersSelectionFlowView;

@SuppressWarnings("serial")
public class AlbaniaSelectionFlowView extends ProductionParametersSelectionFlowView {

	protected AbstractSelectionView createView(SelectableItem[] items) {
		return new DefaultSmallSelectionView(items);
	}

}
