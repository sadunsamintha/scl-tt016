package com.sicpa.tt018.scl.model.productionParameters;

import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.flow.AbstractSelectionFlowModel;
import com.sicpa.standard.sasscl.productionParameterSelection.DefaultSelectionModelFactory;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionParameterRootNode;

public class AlbaniaSelectionModelFactory extends DefaultSelectionModelFactory {
	@Override
	public AbstractSelectionFlowModel create(ProductionParameterRootNode root) {
		return new AlbaniaSelectionModel(root);
	}

}
