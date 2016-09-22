package com.sicpa.standard.sasscl.view.selection.select.barcode;

import com.sicpa.standard.gui.screen.machine.component.IdInput.IdInputListener;
import com.sicpa.standard.gui.screen.machine.component.IdInput.IdInputmodel;

public class BarcodeScreenModel extends IdInputmodel {

	@Override
	public void addIdListener(final IdInputListener listener) {
		if (listener != null) {
			this.listeners.add(IdInputListener.class, listener);
		}
	}
}
