package com.sicpa.standard.sasscl.productionParameterSelection.selectionmodel;

import java.util.List;

import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.SelectableItem;
import com.sicpa.standard.sasscl.productionParameterSelection.node.IProductionParametersNode;

public class ProductionModeSelectionOnlyModel extends DefaultSelectionModel {

	public ProductionModeSelectionOnlyModel(IProductionParametersNode root) {
		super(root);
	}

	@Override
	protected void populate(SelectableItem[] selections, List<SelectableItem> nextOptions) {
		if (selections.length == PRODUCTION_MODE_SCREEN_INDEX) {
			addProductionsMode(nextOptions);
		}
	}

	@Override
	public boolean isLeaf(SelectableItem[] selections) {
		return isSelectionComplete(selections);
	}

	private boolean isSelectionComplete(SelectableItem[] selections) {
		return selections.length > PRODUCTION_MODE_SCREEN_INDEX;
	}

}
