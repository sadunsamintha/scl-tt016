package com.sicpa.standard.sasscl.view.selection.change;

import com.sicpa.standard.client.common.view.mvc.AbstractObservableModel;

public class SelectionChangeViewModel extends AbstractObservableModel {

	protected boolean changedContextEnabled=true;

	public void setChangedContextEnabled(boolean changedContextEnabled) {
		this.changedContextEnabled = changedContextEnabled;
	}

	public boolean isChangedContextEnabled() {
		return changedContextEnabled;
	}
}
