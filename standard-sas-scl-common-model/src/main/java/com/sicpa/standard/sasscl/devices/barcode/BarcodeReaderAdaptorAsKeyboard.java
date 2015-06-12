package com.sicpa.standard.sasscl.devices.barcode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.barcodereader.BarcodeReaderAsKeyboardUtils;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.devices.AbstractDevice;
import com.sicpa.standard.sasscl.devices.DeviceException;
import com.sicpa.standard.sasscl.devices.DeviceStatus;

public class BarcodeReaderAdaptorAsKeyboard extends AbstractDevice {

	private static final Logger logger = LoggerFactory.getLogger(BarcodeReaderAdaptorAsKeyboard.class);

	@Override
	protected void doConnect() throws DeviceException {
		BarcodeReaderAsKeyboardUtils.activate(() -> isWaitingForBarcode(), barcode -> fireBarcodeRead(barcode));
		fireDeviceStatusChanged(DeviceStatus.CONNECTED);
	}

	protected void fireBarcodeRead(final String barcode) {
		logger.info("barcode read from barcode reader: " + barcode);
		EventBusService.post(new BarcodeReaderEvent(barcode));
	}

	@Override
	protected void doDisconnect() throws DeviceException {

	}

	protected boolean waitingForBarcode = false;

	public boolean isWaitingForBarcode() {
		return waitingForBarcode;
	}

	@Subscribe
	public void handleActivityChanged(ApplicationFlowStateChangedEvent evt) {
		waitingForBarcode = evt.getCurrentState().equals(ApplicationFlowState.STT_SELECT_WITH_PREVIOUS)
				|| evt.getCurrentState().equals(ApplicationFlowState.STT_SELECT_NO_PREVIOUS);
	}

}
