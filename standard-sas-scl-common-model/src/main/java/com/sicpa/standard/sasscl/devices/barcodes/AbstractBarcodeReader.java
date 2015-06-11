package com.sicpa.standard.sasscl.devices.barcodes;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.devices.AbstractDevice;
import com.sicpa.standard.sasscl.devices.barcode.BarcodeReaderEvent;
import com.sicpa.standard.sasscl.devices.barcode.IBarcodeReaderAdaptor;

public abstract class AbstractBarcodeReader extends AbstractDevice implements IBarcodeReaderAdaptor {

	public AbstractBarcodeReader() {
		super();
		setName("BarcodeReader");
	}

	protected void fireBarcodeRead(final String barcode) {
		EventBusService.post(new BarcodeReaderEvent(barcode));
	}
}
