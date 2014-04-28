package com.sicpa.standard.sasscl.productionParameterSelection.node.impl;

import javax.swing.ImageIcon;

import com.sicpa.standard.common.util.Messages;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.productionParameterSelection.node.AbstractProductionParametersNode;

public class CodeTypeNode extends AbstractProductionParametersNode<CodeType> {

	private static final long serialVersionUID = 1L;

	public CodeTypeNode(final CodeType codeType) {
		setValue(codeType);
		setText(codeType.getDescription());
		setImage(codeType.getImage());
	}

	public CodeTypeNode() {
	}

	@Override
	public String getFormatedTextForSummary() {
		return getText();
	}

	@Override
	public int getId() {
		return (int) getValue().getId();
	}

	@Override
	public boolean isShownOnSummary() {
		return true;
	}

	@Override
	public void setImage(final ImageIcon image) {
		super.setImage(image);
		getValue().setImage(image);
	}

	@Override
	public String getText() {
		return Messages.get(text);
	}
}
