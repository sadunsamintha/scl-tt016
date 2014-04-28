package com.sicpa.standard.sasscl.productionParameterSelection.node.impl;

import com.sicpa.standard.common.util.Messages;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.productionParameterSelection.node.AbstractProductionParametersNode;

public class ProductionModeNode extends AbstractProductionParametersNode<ProductionMode> {

	private static final long serialVersionUID = 1L;

	public ProductionModeNode(final ProductionMode mode) {
		super();
		setValue(mode);
		setText(mode.getDescription());
	}

	public ProductionModeNode() {
	}

	@Override
	public String getFormatedTextForSummary() {
		return getText();
	}

	@Override
	public int getId() {
		return getValue().getId();
	}

	@Override
	public boolean isShownOnSummary() {
		return true;
	}

	@Override
	public String getText() {
		return Messages.get(text);
	}
}
