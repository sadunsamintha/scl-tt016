package com.sicpa.tt018.scl.view;

import com.sicpa.standard.gui.screen.machine.component.SelectionFlow.flow.DefaultSelectionFlowView;
import com.sicpa.standard.sasscl.view.selection.select.SelectProductionParametersHandPickingView;

@SuppressWarnings("serial")
public class AlbaniaSelectProductionParametersHandPickingView extends SelectProductionParametersHandPickingView {

	public DefaultSelectionFlowView getDelegate() {
		if (delegate == null) {
			delegate = new AlbaniaSelectionFlowView();
			delegate.setUsePreviousPanel(false);
		}
		return delegate;
	}

}
