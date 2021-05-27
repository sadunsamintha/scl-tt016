package com.sicpa.tt085.productionParameterSelection.node.impl;

import com.sicpa.standard.sasscl.productionParameterSelection.node.AbstractProductionParametersNode;
import com.sicpa.tt085.model.TT085Country;

public class CountryNode extends AbstractProductionParametersNode<TT085Country>{
	
	private static final long serialVersionUID = 1L;

	public CountryNode(final TT085Country country) {
		setValue(country);
		setText(country.getDisplayDescription());
		
	}

	public CountryNode() {
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
	public int getId() {
		return getValue().getId().intValue();
	}

}
