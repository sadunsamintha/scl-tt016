package com.sicpa.standard.sasscl.devices.barcodeReader;

import static org.junit.Assert.assertEquals;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.devices.DeviceException;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.devices.DeviceStatusEvent;
import com.sicpa.standard.sasscl.devices.IDeviceStatusListener;
import com.sicpa.standard.sasscl.devices.barcode.BarcodeReaderAdaptor;
import com.sicpa.standard.sasscl.devices.barcode.BarcodeReaderEvent;
import com.sicpa.standard.sasscl.devices.barcode.simulator.BarcodeReaderSimulator;
import com.sicpa.standard.sasscl.devices.barcode.simulator.BarcodeReaderSimulatorConfig;

public class BarcodeReaderSimulatorTest {

	private String barcodeRead;

	private BarcodeReaderSimulatorConfig barcodeConfig;
	private BarcodeReaderSimulator barcodeSimulator;

	private BarcodeReaderAdaptor barcodeController;

	private IDeviceStatusListener deviceStatusListener = Mockito.mock(IDeviceStatusListener.class);

	@Before
	public void setup() {
		barcodeConfig = new BarcodeReaderSimulatorConfig();
		barcodeSimulator = new BarcodeReaderSimulator(barcodeConfig);
		barcodeController = new BarcodeReaderAdaptor(barcodeSimulator);
		barcodeController.addDeviceStatusListener(deviceStatusListener);
	}

	@Test
	public void testReadCode() {

		Object barcodeEventCatcher = new Object() {
			@Subscribe
			public void barcodeRead(BarcodeReaderEvent evt) {
				barcodeRead = evt.getBarcode();
			}
		};
		EventBusService.register(barcodeEventCatcher);
		barcodeController.onBarcodeReceived("123456");

		Assert.assertEquals("123456", barcodeRead);
	}

	@Test
	public void testBarcodeReaderInitialization() throws DeviceException {

		barcodeController.connect();

		// connected event should be received in device status listener
		ArgumentCaptor<DeviceStatusEvent> deviceStatusArgument = ArgumentCaptor.forClass(DeviceStatusEvent.class);

		Mockito.verify(deviceStatusListener, Mockito.atLeastOnce()).deviceStatusChanged(deviceStatusArgument.capture());

		assertEquals(DeviceStatus.CONNECTED, deviceStatusArgument.getValue().getStatus());

		// perform clean up for next test
		barcodeController.disconnect();

		Mockito.verify(deviceStatusListener, Mockito.atLeastOnce()).deviceStatusChanged(deviceStatusArgument.capture());

		// should get disconnected status after the disconnect() method
		assertEquals(DeviceStatus.DISCONNECTED, deviceStatusArgument.getValue().getStatus());

	}
}
