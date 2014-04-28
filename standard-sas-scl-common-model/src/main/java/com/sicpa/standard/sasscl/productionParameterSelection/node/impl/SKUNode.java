package com.sicpa.standard.sasscl.productionParameterSelection.node.impl;

import javax.swing.ImageIcon;

import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.productionParameterSelection.node.AbstractProductionParametersNode;

public class SKUNode extends AbstractProductionParametersNode<SKU> {

	private static final long serialVersionUID = 1L;

	public SKUNode(final SKU sku) {
		setValue(sku);
		setText(sku.getDescription());
		setImage(sku.getImage());
	}

	public SKUNode() {
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
	public void setImage(final ImageIcon image) {
		super.setImage(image);
		getValue().setImage(image);
	}
}
