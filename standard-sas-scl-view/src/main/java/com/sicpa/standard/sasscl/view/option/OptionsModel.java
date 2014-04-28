package com.sicpa.standard.sasscl.view.option;

import com.sicpa.standard.client.common.view.mvc.AbstractObservableModel;

public class OptionsModel extends AbstractObservableModel {

	protected boolean displayOptionEnabled;

	public void setDisplayOptionEnabled(boolean displayOptionEnabled) {
		this.displayOptionEnabled = displayOptionEnabled;
	}

	public boolean isDisplayOptionEnabled() {
		return displayOptionEnabled;
	}

}
