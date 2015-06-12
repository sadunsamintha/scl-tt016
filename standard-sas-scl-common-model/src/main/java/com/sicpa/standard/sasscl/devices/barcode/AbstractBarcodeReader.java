package com.sicpa.standard.sasscl.devices.barcode;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.devices.AbstractDevice;

public abstract class AbstractBarcodeReader extends AbstractDevice implements IBarcodeReaderAdaptor {

	public AbstractBarcodeReader() {
		super();
		setName("BarcodeReader");
	}

	protected void fireBarcodeRead(final String barcode) {
		EventBusService.post(new BarcodeReaderEvent(barcode));
	}
}
