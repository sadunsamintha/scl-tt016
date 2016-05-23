package com.sicpa.standard.sasscl.devices.printer.impl;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.printer.driver.event.PrinterBufferStatusChangedEventArgs;
import com.sicpa.standard.sasscl.devices.DeviceException;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.devices.DeviceStatusEvent;
import com.sicpa.standard.sasscl.devices.IDeviceStatusListener;
import com.sicpa.standard.sasscl.devices.printer.PrinterAdaptorException;
import com.sicpa.standard.sasscl.devices.printer.simulator.PrinterSimulator;
import com.sicpa.standard.sasscl.devices.printer.simulator.PrinterSimulatorConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class PrinterAdaptorTest {

	protected PrinterAdaptor printerAdaptor = null;

	protected IDeviceStatusListener deviceStatusListener = Mockito.mock(IDeviceStatusListener.class);

	@Before
	public void setup() {
		PrinterSimulatorConfig config = new PrinterSimulatorConfig();
		config.setPort(6523);
		PrinterSimulator controller = new PrinterSimulator(config);
		printerAdaptor = new PrinterAdaptorDomino();
		printerAdaptor.setController(controller);
		printerAdaptor.addDeviceStatusListener(deviceStatusListener);
	}

	@Test
	public void testStartPrinterWithoutConnect() {
		// start without connect
		try {
			printerAdaptor.start();
		} catch (PrinterAdaptorException e) {
			Assert.assertTrue(e.getMessage().contains("error when start printing"));
		}
	}

	@Test
	public void testPrinterStopFailed() throws DeviceException {
		try {
			printerAdaptor.stop();
		} catch (PrinterAdaptorException e) {
			Assert.assertEquals("error when stop printing", e.getMessage());
		}
	}

	// @Test
	// public void testFaultStatus() {
	// DummyDeviceMessageListener listener = new DummyDeviceMessageListener();
	// EventBusService.register(listener);
	//
	// FaultStatus status = new FaultStatus(2); // status.isChargeFault()
	// // invoke fault status
	// printerAdaptor.onFaultStatusChanged(null, status);
	// Assert.assertEquals(1, listener.getCounter());
	//
	// status = new FaultStatus(4); // status.isGutterFault()
	// // invoke fault status
	// printerAdaptor.onFaultStatusChanged(null, status);
	// Assert.assertEquals(2, listener.getCounter());
	//
	// status = new FaultStatus(1); // status.isHighVoltageFault()
	// // invoke fault status
	// printerAdaptor.onFaultStatusChanged(null, status);
	// Assert.assertEquals(3, listener.getCounter());
	//
	// status = new FaultStatus(32); // status.isMakeUpCartridgeLow()
	// // invoke fault status
	// printerAdaptor.onFaultStatusChanged(null, status);
	// Assert.assertEquals(4, listener.getCounter());
	//
	// status = new FaultStatus(64); // status.isInkCartridgeLow()
	// // invoke fault status
	// printerAdaptor.onFaultStatusChanged(null, status);
	// Assert.assertEquals(5, listener.getCounter());
	//
	// // reset counter
	// listener.setCounter(0);
	//
	// status = new FaultStatus(128); // status.isReservoirLevelTooLow()
	// // invoke fault status
	// printerAdaptor.onFaultStatusChanged(null, status);
	// Assert.assertEquals(1, listener.getCounter());
	//
	// }

	// @Test
	// public void testInkStatus() {
	// DummyDeviceMessageListener listener = new DummyDeviceMessageListener();
	// EventBusService.register(listener);
	//
	// InkStatus status = new InkStatus(4); // status.isInkSystemFault()
	// // invoke fault status
	// printerAdaptor.onInkStatusChanged(null, status);
	// Assert.assertEquals(1, listener.getCounter());
	//
	// status = new InkStatus(16); // status.isViscometerError()
	// // invoke fault status
	// printerAdaptor.onInkStatusChanged(null, status);
	// Assert.assertEquals(2, listener.getCounter());
	//
	// status = new InkStatus(8); // status.isInkReservoirTimedOut()
	// // invoke fault status
	// printerAdaptor.onInkStatusChanged(null, status);
	// Assert.assertEquals(3, listener.getCounter());
	//
	// status = new InkStatus(128); // status.isInkReservoirLessThan2Hours()
	// // invoke fault status
	// printerAdaptor.onInkStatusChanged(null, status);
	// Assert.assertEquals(4, listener.getCounter());
	//
	// status = new InkStatus(32); // status.isInkReservoirLessThan24Hours()
	// // invoke fault status
	// printerAdaptor.onInkStatusChanged(null, status);
	// Assert.assertEquals(5, listener.getCounter());
	//
	// // reset counter
	// listener.setCounter(0);
	//
	// status = new InkStatus(2); // status.isViscosityOutOfRange()
	// printerAdaptor.onInkStatusChanged(null, status);
	// Assert.assertEquals(1, listener.getCounter());
	//
	// status = new InkStatus(512); // status.isPumpOutOfRange()
	// printerAdaptor.onInkStatusChanged(null, status);
	// Assert.assertEquals(2, listener.getCounter());
	//
	// status = new InkStatus(16777472); // status.isWatchdogReset()
	// printerAdaptor.onInkStatusChanged(null, status);
	// Assert.assertEquals(3, listener.getCounter());
	//
	// status = new InkStatus(64);
	// printerAdaptor.onInkStatusChanged(null, status);
	// Assert.assertEquals(4, listener.getCounter());
	// }

	// for some reason this test fails from time to time...
	@Ignore
	public void testPrinterInitialization() throws DeviceException {
		printerAdaptor.connect();

		long firstTime = System.currentTimeMillis();
		while (!printerAdaptor.isConnected()) {
			if (System.currentTimeMillis() - firstTime > 30000) {
				Assert.fail("timeout connecting to printer");
			}
			ThreadUtils.sleepQuietly(1);
		}

		// connected event should be received in device status listener
		ArgumentCaptor<DeviceStatusEvent> deviceStatusArgument = ArgumentCaptor.forClass(DeviceStatusEvent.class);

		Mockito.verify(deviceStatusListener, Mockito.atLeastOnce()).deviceStatusChanged(deviceStatusArgument.capture());

		assertEquals(DeviceStatus.CONNECTED, deviceStatusArgument.getValue().getStatus());

		printerAdaptor.start();
		printerAdaptor.sendCodesToPrint(Arrays.asList("132"));
		printerAdaptor.onPrinterCodesNeeded(null, new PrinterBufferStatusChangedEventArgs().getCodeNeeded());

		Mockito.verify(deviceStatusListener, Mockito.atLeastOnce()).deviceStatusChanged(deviceStatusArgument.capture());
		assertEquals(DeviceStatus.STARTED, deviceStatusArgument.getValue().getStatus());

		printerAdaptor.stop();
		Mockito.verify(deviceStatusListener, Mockito.atLeastOnce()).deviceStatusChanged(deviceStatusArgument.capture());
		assertEquals(DeviceStatus.STOPPED, deviceStatusArgument.getValue().getStatus());

		// perform clean up for next test
		printerAdaptor.disconnect();

		Mockito.verify(deviceStatusListener, Mockito.atLeastOnce()).deviceStatusChanged(deviceStatusArgument.capture());

		// should get disconnected status after the disconnect() method
		assertEquals(DeviceStatus.DISCONNECTED, deviceStatusArgument.getValue().getStatus());

	}

	static class DummyDeviceMessageListener {

		private volatile int counter = 0;

		@Subscribe
		public void notifyMessage(MessageEvent evt) {
			counter++;
		}

		public int getCounter() {
			return counter;
		}

		public void setCounter(int counter) {
			this.counter = counter;
		}
	}
}