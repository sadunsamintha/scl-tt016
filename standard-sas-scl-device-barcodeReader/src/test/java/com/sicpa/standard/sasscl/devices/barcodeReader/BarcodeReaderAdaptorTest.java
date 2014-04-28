package com.sicpa.standard.sasscl.devices.barcodeReader;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.Socket;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;

import com.sicpa.standard.barcode.reader.controller.internal.BarcodeReaderControllerImpl;
import com.sicpa.standard.barcode.reader.driver.internal.simulator.BarcodeSerialPortSimulator;
import com.sicpa.standard.barcode.reader.driver.model.Gryphon130DriverCommands;
import com.sicpa.standard.barcode.reader.model.BarcodeReaderModel;
import com.sicpa.standard.barcode.reader.model.IBarcodeReaderParameter;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.sasscl.devices.DeviceException;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.devices.DeviceStatusEvent;
import com.sicpa.standard.sasscl.devices.IDeviceStatusListener;
import com.sicpa.standard.sasscl.devices.barcode.BarcodeReaderAdaptor;

public class BarcodeReaderAdaptorTest {

	private BarcodeReaderAdaptor controller;

	private BarcodeReaderModel<IBarcodeReaderParameter> model = new BarcodeReaderModel<IBarcodeReaderParameter>();

	private static BarcodeSerialPortSimulator mSimulator;

	private IDeviceStatusListener deviceStatusListener = Mockito.mock(IDeviceStatusListener.class);

	private static int port = 9876;

	@BeforeClass
	public static void setupServer() {

		mSimulator = new BarcodeSerialPortSimulator(port);
		mSimulator.start();
	}

	@Before
	public void setup() {

		model.setName("BarcodeTest");
		model.setParserName("Gryphon130BarcodeParser");
		model.setDriverName("Gryphon130Driver");
		model.setSimulator(true);
		model.setSimulatorHost("localhost");
		model.setLifeCheckInterval(1000);
		model.setPort(port + "");
		model.setBarcodeReaderParameters(new Gryphon130DriverCommands());

		BarcodeReaderControllerImpl barcodeReaderController = new BarcodeReaderControllerImpl();
		barcodeReaderController.setModel(model);

		controller = new BarcodeReaderAdaptor(barcodeReaderController);
		controller.addDeviceStatusListener(deviceStatusListener);
	}

	@Test
	public void initTest() throws InterruptedException, DeviceException, IOException {
		// connect barcode reader
		controller.connect();

		long firstTime = System.currentTimeMillis();
		while (!controller.getStatus().equals(DeviceStatus.CONNECTED)) {
			if (System.currentTimeMillis() - firstTime > 30000) {
				Assert.fail("timeout connecting to barcodereader");
			}
			ThreadUtils.sleepQuietly(1);
		}

		// disconnect barcode reader
		controller.disconnect();

		mSimulator.shutdown();

		Socket s = null;

		try {
			s = new Socket("localhost", port);

			((Thread) Whitebox.getInternalState(mSimulator, "mServerThread")).join();

			// connected event should be received in device status listener
			ArgumentCaptor<DeviceStatusEvent> deviceStatusArgument = ArgumentCaptor.forClass(DeviceStatusEvent.class);

			Mockito.verify(deviceStatusListener, Mockito.atLeastOnce()).deviceStatusChanged(
					deviceStatusArgument.capture());

			// should get disconnected status after the disconnect() method
			assertEquals(DeviceStatus.DISCONNECTED, deviceStatusArgument.getValue().getStatus());
		} finally {
			if (null != s)
				s.close();
		}
	}
}
