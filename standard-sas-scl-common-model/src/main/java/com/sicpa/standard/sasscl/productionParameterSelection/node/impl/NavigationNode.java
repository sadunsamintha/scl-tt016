package com.sicpa.standard.sasscl.productionParameterSelection.node.impl;

import com.sicpa.standard.sasscl.productionParameterSelection.node.AbstractProductionParametersNode;

public class NavigationNode extends AbstractProductionParametersNode<String> {

	private static final long serialVersionUID = 1L;

	public NavigationNode(String text) {
		setText(text);
	}

	@Override
	public boolean isShownOnSummary() {
		return false;
	}

	@Override
	public String getFormatedTextForSummary() {
		return null;
	}

	@Override
	public int getId() {
		return 0;
	}
	
	@Override
	public String getText() {
		return text;
	}

}
