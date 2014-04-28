package com.sicpa.standard.sasscl.view.snapshot;

import com.sicpa.standard.client.common.view.mvc.AbstractObservableModel;

public class SnapshotViewModel extends AbstractObservableModel {

	protected boolean busy;

	public void setBusy(boolean busy) {
		this.busy = busy;
	}

	public boolean isBusy() {
		return busy;
	}

}
