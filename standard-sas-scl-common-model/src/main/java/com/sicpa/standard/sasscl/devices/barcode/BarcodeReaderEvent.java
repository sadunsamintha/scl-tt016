package com.sicpa.standard.sasscl.devices.barcode;

/**
 * Event that defines the barcodes read by barcode reader.
 * 
 * 
 */
public class BarcodeReaderEvent {

	protected String barcode;

	public BarcodeReaderEvent(final String barcode) {
		super();
		this.barcode = barcode;
	}

	public String getBarcode() {
		return this.barcode;
	}

}
