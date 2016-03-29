package com.sicpa.tt018.scl.model.productionParameters.impl;

import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.productionParameterSelection.node.IProductionParametersNode;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionModeNode;

public class AlbaniaProductionModeNode extends ProductionModeNode {
	private static final long serialVersionUID = -7218511969538010310L;

	public AlbaniaProductionModeNode(final ProductionMode mode) {
		super(mode);
		setText(mode.getDescription());
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || !(obj instanceof AlbaniaProductionModeNode)) {
			return false;
		}
		final AlbaniaProductionModeNode node = (AlbaniaProductionModeNode) obj;
		if (getText() != null) {
			return getText().equals(node.getText());
		}
		return super.equals(obj);
	}

	public IProductionParametersNode getChild(final String text) {
		// NULL arg & children exist check
		if (text == null || getChildren() == null) {
			return null;
		}
		// Look through children node
		for (final IProductionParametersNode node : getChildren()) {
			if (text.equals(node.getText())) {
				return node;
			}
		}
		// No node found
		return null;
	}

}
