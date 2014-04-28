package com.sicpa.standard.sasscl.view.selection.display;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.sasscl.controller.ProductionParametersEvent;
import com.sicpa.standard.sasscl.model.ProductionParameters;

public class SelectionDisplayViewController implements ISelectionDisplayViewListener {

	protected SelectionDisplayModel model;

	public SelectionDisplayViewController() {
		this(new SelectionDisplayModel());
	}

	public SelectionDisplayViewController(SelectionDisplayModel model) {
		this.model = model;
	}

	public void setProductionParameters(ProductionParameters productionParameters) {
		model.setProductionParameters(productionParameters);
		model.notifyModelChanged();
	}

	@Subscribe
	public void productionParametersChanged(ProductionParametersEvent evt) {
		model.notifyModelChanged();
	}

	public SelectionDisplayModel getModel() {
		return model;
	}
}
