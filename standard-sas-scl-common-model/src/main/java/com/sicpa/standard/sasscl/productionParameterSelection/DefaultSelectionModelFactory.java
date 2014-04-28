package com.sicpa.standard.sasscl.productionParameterSelection;

import java.util.ArrayList;
import java.util.List;

import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.flow.AbstractSelectionFlowModel;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionParameterRootNode;

public class DefaultSelectionModelFactory implements ISelectionModelFactory {

	protected final List<IConfigFlowModel> configtasks = new ArrayList<IConfigFlowModel>();

	@Override
	public AbstractSelectionFlowModel create(ProductionParameterRootNode root) {
		if (root != null) {
			AbstractSelectionFlowModel model = new SelectionModel(root);
			for (IConfigFlowModel task : configtasks) {
				task.config(model);
			}
			return model;
		} else {
			return null;
		}
	}

	@Override
	public void addConfigFlowModelTask(IConfigFlowModel task) {
		configtasks.add(task);
	}
}
