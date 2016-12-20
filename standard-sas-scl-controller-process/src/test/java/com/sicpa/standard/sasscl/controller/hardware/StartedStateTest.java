package com.sicpa.standard.sasscl.controller.hardware;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.controller.hardware.state.impl.StartedState;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.devices.DeviceStatusEvent;
import com.sicpa.standard.sasscl.devices.IStartableDevice;
import com.sicpa.standard.sasscl.devices.plc.IPlcAdaptor;
import com.sicpa.standard.sasscl.devices.plc.PlcAdaptorException;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.test.utils.TestHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

public class StartedStateTest {

	private StartedState startedState;
	private IHardwareControllerState stoppingState;

	private HardwareController conveyorController;

	private IPlcAdaptor plc;
	private IStartableDevice startableDevice1;
	private IStartableDevice startableDevice2;
	private List<IStartableDevice> startableDevices;

	@Before
	public void setup() {
		TestHelper.initExecutor();

		startedState = new StartedState();

		conveyorController = Mockito.mock(HardwareController.class);

		plc = Mockito.mock(IPlcAdaptor.class);
		startableDevice1 = Mockito.mock(IStartableDevice.class);
		startableDevice2 = Mockito.mock(IStartableDevice.class);
		startableDevices = Arrays.asList(startableDevice1, startableDevice2);

		startedState.setStoppingState(stoppingState);

		startedState.setPlc(plc);
		startedState.setSetter(conveyorController);
		startedState.setStartableDevices(startableDevices);
	}

	@Test
	public void testEnter() throws PlcAdaptorException {
		Mockito.when(plc.isConnected()).thenReturn(true);

		EventBusService.register(new Object() {
			@Subscribe
			public void handleEvent(HardwareControllerStatusEvent evt) {
				Assert.assertEquals(HardwareControllerStatus.STARTED, evt.getStatus());
			}
		});
		startedState.enter();
		TestHelper.runAllTasks();

		Mockito.verify(plc).start();
		Mockito.verify(plc).doRun();
	}

	@Test
	public void testStop() {
		startedState.stop();
		TestHelper.runAllTasks();
		Mockito.verify(conveyorController).setCurrentState(stoppingState);
	}

	@Test
	public void testBlockProductionDeviceDisconnect() {
		setStartableDevice(startableDevice1, true, DeviceStatus.DISCONNECTED);
		setStartableDevice(startableDevice2, true, DeviceStatus.CONNECTED);

		startedState.deviceStatusChanged(new DeviceStatusEvent(DeviceStatus.DISCONNECTED, startableDevice1));
		TestHelper.runAllTasks();
		Mockito.verify(conveyorController).setCurrentState(stoppingState);
	}

	@Test
	public void testNonBlockProductionDeviceDisconnect() {
		EventBusService.register(new Object() {
			@Subscribe
			public void handleEvent(MessageEvent evt) {
				Assert.assertEquals(MessageEventKey.DevicesController.DEVICE_DISCONNECT_WARNING, evt.getKey());
			}
		});

		setStartableDevice(startableDevice1, false, DeviceStatus.DISCONNECTED);
		setStartableDevice(startableDevice2, true, DeviceStatus.CONNECTED);
		Mockito.when(plc.getStatus()).thenReturn(DeviceStatus.CONNECTED);

		startedState.deviceStatusChanged(new DeviceStatusEvent(DeviceStatus.DISCONNECTED, startableDevice1));
		TestHelper.runAllTasks();
	}

	private void setStartableDevice(IStartableDevice startableDevice, boolean blockProduction,
									DeviceStatus deviceStatus) {
		Mockito.when(startableDevice.getStatus()).thenReturn(deviceStatus);
		Mockito.when(startableDevice.isBlockProductionStart()).thenReturn(blockProduction);
	}
}
