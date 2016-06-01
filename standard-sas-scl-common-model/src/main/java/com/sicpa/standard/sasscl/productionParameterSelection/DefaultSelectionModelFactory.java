package com.sicpa.standard.sasscl.productionParameterSelection;

import java.util.ArrayList;
import java.util.List;

import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.flow.AbstractSelectionFlowModel;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionParameterRootNode;
import com.sicpa.standard.sasscl.productionParameterSelection.selectionmodel.DefaultSelectionModel;
import com.sicpa.standard.sasscl.productionParameterSelection.selectionmodel.ProductionModeSelectionOnlyModel;

public class DefaultSelectionModelFactory implements ISelectionModelFactory {

	private final List<IConfigFlowModel> configtasks = new ArrayList<>();
	private boolean productionModeOnlySelection;

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
		if (productionModeOnlySelection) {
			return new ProductionModeSelectionOnlyModel(root);
		} else {
			return new DefaultSelectionModel(root);
		}
	}

	private void configModel(AbstractSelectionFlowModel model) {
		for (IConfigFlowModel task : configtasks) {
			task.config(model);
		}
	}

	@Override
	public void addConfigFlowModelTask(IConfigFlowModel task) {
		configtasks.add(task);
	}

	public void setProductionModeOnlySelection(boolean productionModeOnlySelection) {
		this.productionModeOnlySelection = productionModeOnlySelection;
	}
}
