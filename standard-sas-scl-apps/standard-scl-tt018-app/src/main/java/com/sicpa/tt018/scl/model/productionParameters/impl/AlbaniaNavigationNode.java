package com.sicpa.tt018.scl.model.productionParameters.impl;

import com.sicpa.standard.sasscl.productionParameterSelection.node.IProductionParametersNode;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.NavigationNode;

public class AlbaniaNavigationNode extends NavigationNode {
	private static final long serialVersionUID = -7218511969538010310L;

	public AlbaniaNavigationNode(final String text) {
		super(text);
	}

	@Override
	public boolean isShownOnSummary() {
		return true;
	}

	@Override
	public String getFormatedTextForSummary() {
		return getText();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || !(obj instanceof AlbaniaNavigationNode)) {
			return false;
		}
		final AlbaniaNavigationNode node = (AlbaniaNavigationNode) obj;
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
