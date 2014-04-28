package com.sicpa.standard.sasscl.productionParameterSelection;

import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.flow.AbstractSelectionFlowModel;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionParameterRootNode;

public interface ISelectionModelFactory {

	AbstractSelectionFlowModel create(ProductionParameterRootNode root);

	void addConfigFlowModelTask(IConfigFlowModel task);

	public interface IConfigFlowModel {
		void config(AbstractSelectionFlowModel flowmodel);
	}

}
