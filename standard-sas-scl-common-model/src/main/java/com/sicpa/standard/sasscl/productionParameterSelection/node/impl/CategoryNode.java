package com.sicpa.standard.sasscl.productionParameterSelection.node.impl;

import javax.swing.ImageIcon;

import com.sicpa.standard.common.util.Messages;
import com.sicpa.standard.sasscl.productionParameterSelection.node.AbstractProductionParametersNode;

public class CategoryNode extends AbstractProductionParametersNode<Object> {

	private static final long serialVersionUID = 1L;
	protected int id;

	public CategoryNode(final int id, final String text, final ImageIcon image) {
		this.id = id;
		setText(text);
		setImage(image);
	}

	@Override
	public String getFormatedTextForSummary() {
		return "";
	}

	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public boolean isShownOnSummary() {
		return false;
	}

	@Override
	public String getText() {
		return Messages.get(text);
	}
}
