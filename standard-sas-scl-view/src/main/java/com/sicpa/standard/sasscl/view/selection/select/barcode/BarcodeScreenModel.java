package com.sicpa.standard.sasscl.view.selection.select.barcode;

import com.sicpa.standard.gui.screen.machine.component.IdInput.IdInputListener;
import com.sicpa.standard.gui.screen.machine.component.IdInput.IdInputmodel;

public class BarcodeScreenModel extends IdInputmodel {

	protected boolean barcodeConnected;

	public boolean isBarcodeConnected() {
		return this.barcodeConnected;
	}

	public void setBarcodeConnected(final boolean barcodeConnected) {
		this.barcodeConnected = barcodeConnected;
		fireBarcodeConnectionChanged(barcodeConnected);
	}

	@Override
	public void addIdListener(final IdInputListener listener) {
		if (listener instanceof IBarcodeViewListener) {
			this.listeners.add(IBarcodeViewListener.class, (IBarcodeViewListener) listener);
		} else {
			super.addIdListener(listener);
		}
	}

	protected void fireBarcodeConnectionChanged(final boolean connected) {
		Object[] listeners = this.listeners.getListenerList();
		BarcodeViewEvent e = null;
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == IBarcodeViewListener.class) {
				if (e == null) {
					e = new BarcodeViewEvent(connected);
				}
				((IBarcodeViewListener) listeners[i + 1]).barcodeConnectionStatusChanged(e);
			}
		}
	}
}
