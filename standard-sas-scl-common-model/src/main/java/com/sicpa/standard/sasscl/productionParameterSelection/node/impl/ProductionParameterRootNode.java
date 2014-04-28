package com.sicpa.standard.sasscl.productionParameterSelection.node.impl;

import java.util.List;

import javax.swing.ImageIcon;

import com.sicpa.standard.sasscl.productionParameterSelection.node.AbstractProductionParametersNode;
import com.sicpa.standard.sasscl.productionParameterSelection.node.IProductionParametersNode;

public class ProductionParameterRootNode extends AbstractProductionParametersNode<Object> {

	private static final long serialVersionUID = 1L;

	public ProductionParameterRootNode() {
	}

	@Override
	public List<IProductionParametersNode> getChildren() {
		return this.children;
	}

	@Override
	public ImageIcon getImage() {
		return null;
	}

	@Override
	public String getText() {
		return null;
	}

	@Override
	public Object getValue() {
		return null;
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
	public boolean isShownOnSummary() {
		return false;
	}

	@Override
	public boolean isLeaf() {
		return this.children == null || this.children.isEmpty();
	}
}
