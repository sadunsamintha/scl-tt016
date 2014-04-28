package com.sicpa.standard.sasscl.devices.barcode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.barcode.reader.controller.IBarcodeReaderController;
import com.sicpa.standard.barcode.reader.controller.IBarcodeReaderControllerListener;
import com.sicpa.standard.barcode.reader.driver.event.BarcodeReaderDriverEventArgs;
import com.sicpa.standard.barcode.reader.driver.event.BarcodeReaderDriverEventCode;
import com.sicpa.standard.barcode.reader.model.IBarcodeReaderModel;
import com.sicpa.standard.sasscl.devices.DeviceException;
import com.sicpa.standard.sasscl.devices.DeviceStatus;

public class BarcodeReaderAdaptor extends AbstractBarcodeReader implements IBarcodeReaderControllerListener {

	private final static Logger logger = LoggerFactory.getLogger(BarcodeReaderAdaptor.class);

	/**
	 * use standard barcode controller
	 * 
	 * delegate device communication to standard barcode component
	 */
	protected IBarcodeReaderController<? extends IBarcodeReaderModel> controller = null;

	/**
	 * 
	 * @param barcodeController
	 */
	public BarcodeReaderAdaptor(IBarcodeReaderController<? extends IBarcodeReaderModel> barcodeController) {
		this.controller = barcodeController;
		this.controller.addListener(this);
	}

	@Override
	protected void doConnect() throws DeviceException {
		try {
			this.controller.create();
		} catch (com.sicpa.standard.barcode.reader.controller.BarcodeReaderException e) {
			throw new BarcodeReaderException(e);
		}
	}

	@Override
	protected void doDisconnect() throws DeviceException {
		try {
			this.controller.shutdown();
		} catch (com.sicpa.standard.barcode.reader.controller.BarcodeReaderException e) {
			throw new BarcodeReaderException(e);
		} finally {
			fireDeviceStatusChanged(DeviceStatus.DISCONNECTED);
		}
	}

	@Override
	public void onBarcodeReaderEventReceived(final IBarcodeReaderController<?> controller,
			final BarcodeReaderDriverEventArgs eventArgs) {
		// convert event driver code
		BarcodeReaderDriverEventCode eventCode = BarcodeReaderDriverEventCode.valueOf(eventArgs.getEventCode());
		
		logger.debug("Barcode reader - event status changed: {}", eventCode.name());

		switch (eventCode) {

		case CONNECTED:
			fireDeviceStatusChanged(DeviceStatus.CONNECTED);
			break;

		case FAILED_TO_CONNECT_DEVICE:
		case DISCONNECTED:
			fireDeviceStatusChanged(DeviceStatus.DISCONNECTED);
			break;
		}
	}

	@Override
	public void onBarcodeReceived(final String barcode) {
		logger.debug("Barcode received: {}", barcode);
		fireBarcodeRead(barcode);
	}

	protected IBarcodeReaderController<?> getController() {
		return this.controller;
	}
}
