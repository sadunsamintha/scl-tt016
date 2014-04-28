package com.sicpa.standard.sasscl.controller.view.event;

public class BarcodeReadEvent {
	protected String barcode;

	public BarcodeReadEvent(final String barcode) {
		super();
		this.barcode = barcode;
	}

	public String getBarcode() {
		return this.barcode;
	}
}
