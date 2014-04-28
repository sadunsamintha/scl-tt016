package com.sicpa.standard.sasscl.view.startstop;

import com.sicpa.standard.client.common.view.mvc.AbstractObservableModel;

public class StartStopModel extends AbstractObservableModel {

	protected boolean startEnabled;
	protected boolean stopEnabled;

	public boolean isStartEnabled() {
		return startEnabled;
	}

	public void setStartEnabled(boolean startEnabled) {
		this.startEnabled = startEnabled;
	}

	public boolean isStopEnabled() {
		return stopEnabled;
	}

	public void setStopEnabled(boolean stopEnabled) {
		this.stopEnabled = stopEnabled;
	}
}
