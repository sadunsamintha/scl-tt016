package com.sicpa.standard.sasscl.devices.barcode;

import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_SELECT_NO_PREVIOUS;
import static com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState.STT_SELECT_WITH_PREVIOUS;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;

import javax.swing.text.JTextComponent;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.gui.listener.CoalescentChangeListener;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.devices.DeviceException;
import com.sicpa.standard.sasscl.devices.DeviceStatus;

public class BarcodeReaderAdaptorAsKeyboard extends AbstractBarcodeReader {

	protected String barcode = "";

	@Override
	protected void doConnect() throws DeviceException {
		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
			@Override
			public void eventDispatched(AWTEvent event) {

				if (event instanceof KeyEvent) {
					if (isEventAccepted((KeyEvent) event)) {
						// the textfield handle it directly
						return;
					}
					// if not a text field do not let the event reach the destination
					((KeyEvent) event).consume();

					if (!waitingForBarcode) {
						return;
					}

					if (event.getID() == KeyEvent.KEY_RELEASED) {
						if (((KeyEvent) event).getKeyCode() == KeyEvent.VK_ENTER) {
							barcode = barcode.trim();
							if (!barcode.isEmpty()) {
								fireBarcodeRead(barcode);
								barcode = "";
							}
						}
					} else if (event.getID() == KeyEvent.KEY_TYPED) {
						barcode += ((KeyEvent) event).getKeyChar();
					}
					keyEventCleaner.eventReceived();
				}
			}
		}, AWTEvent.KEY_EVENT_MASK);

		fireDeviceStatusChanged(DeviceStatus.CONNECTED);
	}

	@Override
	protected void doDisconnect() throws DeviceException {

	}

	protected boolean waitingForBarcode = false;

	protected boolean isEventAccepted(KeyEvent evt) {
		return evt.getComponent() instanceof JTextComponent;
	}

	@Subscribe
	public void handleActivityChanged(ApplicationFlowStateChangedEvent evt) {
		waitingForBarcode = evt.getCurrentState().equals(STT_SELECT_WITH_PREVIOUS)
				|| evt.getCurrentState().equals(STT_SELECT_NO_PREVIOUS);
	}

	protected CoalescentChangeListener keyEventCleaner = new CoalescentChangeListener(100) {
		@Override
		public void doAction() {
			barcode = "";
		}
	};
}
