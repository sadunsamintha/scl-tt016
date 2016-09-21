package com.sicpa.tt018.scl.view;

import com.sicpa.standard.sasscl.view.selection.select.SelectProductionParametersHandPickingView;
import com.sicpa.standard.sasscl.view.selection.select.productionparameters.ProductionParametersSelectionFlowView;

@SuppressWarnings("serial")
public class AlbaniaSelectProductionParametersHandPickingView extends SelectProductionParametersHandPickingView {

	public ProductionParametersSelectionFlowView getDelegate() {
		if (delegate == null) {
			delegate = new AlbaniaSelectionFlowView();
		}
		return delegate;
	}

}
