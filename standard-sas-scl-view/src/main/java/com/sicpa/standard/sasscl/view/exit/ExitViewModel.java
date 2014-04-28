package com.sicpa.standard.sasscl.view.exit;

import com.sicpa.standard.client.common.view.mvc.AbstractObservableModel;

public class ExitViewModel extends AbstractObservableModel {

	protected boolean exitButtonEnabled = true;

	public boolean isExitButtonEnabled() {
		return exitButtonEnabled;
	}

	public void setExitButtonEnabled(boolean exitButtonEnabled) {
		this.exitButtonEnabled = exitButtonEnabled;
	}

}
