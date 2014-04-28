package com.sicpa.standard.sasscl.view.selection.select.barcode;

import com.sicpa.standard.gui.screen.machine.component.IdInput.IdInputListener;

public interface IBarcodeViewListener extends IdInputListener {

	void barcodeConnectionStatusChanged(BarcodeViewEvent evt);

}
