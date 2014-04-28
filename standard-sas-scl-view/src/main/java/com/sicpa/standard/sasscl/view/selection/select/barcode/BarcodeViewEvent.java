package com.sicpa.standard.sasscl.view.selection.select.barcode;

import com.sicpa.standard.gui.screen.machine.component.IdInput.IdInputEvent;

public class BarcodeViewEvent extends IdInputEvent {

	protected boolean connected;

	public BarcodeViewEvent(final boolean connected) {
		super("", "");
		setConnected(connected);
	}

	public boolean isConnected() {
		return this.connected;
	}

	public void setConnected(final boolean connected) {
		this.connected = connected;
	}
}
