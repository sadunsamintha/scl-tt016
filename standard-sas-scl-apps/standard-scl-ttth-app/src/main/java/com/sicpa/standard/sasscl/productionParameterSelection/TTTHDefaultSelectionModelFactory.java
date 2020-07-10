package com.sicpa.standard.sasscl.productionParameterSelection;

import java.util.ArrayList;
import java.util.List;

import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.flow.AbstractSelectionFlowModel;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionParameterRootNode;
import com.sicpa.standard.sasscl.productionParameterSelection.selectionmodel.DefaultSelectionModel;
import com.sicpa.standard.sasscl.productionParameterSelection.selectionmodel.ProductionModeSelectionOnlyModel;

public class TTTHDefaultSelectionModelFactory implements ISelectionModelFactory {

	private final String markingType = "Beer Marking";

	private final List<IConfigFlowModel> configTasks = new ArrayList<>();

	@Override
	public AbstractSelectionFlowModel create(ProductionParameterRootNode root) {
		if (root != null) {
			AbstractSelectionFlowModel model = createModel(root);
			configModel(model);
			return model;
		} else {
			return null;
		}
	}

	private AbstractSelectionFlowModel createModel(ProductionParameterRootNode root) {
		if (root.getChildren().stream().anyMatch(node -> node.getText().equals(markingType))) {
			return new DefaultSelectionModel(root);
		} else {
			return new ProductionModeSelectionOnlyModel(root);
		}
	}

	private void configModel(AbstractSelectionFlowModel model) {
		for (IConfigFlowModel task : configTasks) {
			task.config(model);
		}
	}

	@Override
	public void addConfigFlowModelTask(IConfigFlowModel task) {
		configTasks.add(task);
	}
}
