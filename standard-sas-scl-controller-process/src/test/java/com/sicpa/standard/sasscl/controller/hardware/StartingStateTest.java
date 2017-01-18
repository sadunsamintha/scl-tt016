package com.sicpa.standard.sasscl.controller.hardware;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.controller.hardware.state.impl.StartingState;
import com.sicpa.standard.sasscl.devices.DeviceException;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.devices.DeviceStatusEvent;
import com.sicpa.standard.sasscl.devices.IStartableDevice;
import com.sicpa.standard.sasscl.devices.plc.IPlcAdaptor;
import com.sicpa.standard.sasscl.test.utils.TestHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

public class StartingStateTest {

	private StartingState startingState;

	private IHardwareControllerState startedState;
	private IHardwareControllerState connectingState;
	private IHardwareControllerState disconnectingState;

	private HardwareController conveyorController;

	private IPlcAdaptor plc;
	private IStartableDevice startableDevice1;
	private IStartableDevice startableDevice2;
	private List<IStartableDevice> startableDevices;

	@Before
	public void setup() {
		TestHelper.initExecutor();

		startingState = new StartingState();
		startedState = Mockito.mock(IHardwareControllerState.class);
		connectingState = Mockito.mock(IHardwareControllerState.class);
		disconnectingState = Mockito.mock(IHardwareControllerState.class);

		conveyorController = Mockito.mock(HardwareController.class);

		plc = Mockito.mock(IPlcAdaptor.class);
		startableDevice1 = Mockito.mock(IStartableDevice.class);
		startableDevice2 = Mockito.mock(IStartableDevice.class);
		startableDevices = Arrays.asList(startableDevice1, startableDevice2);

		startingState.setConnectingState(connectingState);
		startingState.setDisconnectingState(disconnectingState);
		startingState.setStartedState(startedState);

		startingState.setPlc(plc);
		startingState.setSetter(conveyorController);
		startingState.setStartableDevices(startableDevices);

	}

	@Test
	public void testEnter() throws DeviceException {
		Mockito.when(startableDevice1.isConnected()).thenReturn(true);
		Mockito.when(startableDevice2.isConnected()).thenReturn(true);

		EventBusService.register(new Object() {
			@Subscribe
			public void handleEvent(HardwareControllerStatusEvent evt) {
				Assert.assertEquals(HardwareControllerStatus.STARTING, evt.getStatus());
			}
		});
		startingState.enter();
		TestHelper.runAllTasks();

		Mockito.verify(startableDevice1).start();
		Mockito.verify(startableDevice2).start();
	}

	@Test
	public void testDisconnect() throws DeviceException {
		startingState.disconnect();
		TestHelper.runAllTasks();
		Mockito.verify(conveyorController).setCurrentState(disconnectingState);
	}

	@Test
	public void testDeviceStatusChangedAllStarted() {
		Mockito.when(startableDevice1.getStatus()).thenReturn(DeviceStatus.STARTED);
		Mockito.when(startableDevice2.getStatus()).thenReturn(DeviceStatus.STARTED);
		Mockito.when(plc.getStatus()).thenReturn(DeviceStatus.STARTED);

		Mockito.when(startableDevice1.isBlockProductionStart()).thenReturn(true);
		Mockito.when(startableDevice2.isBlockProductionStart()).thenReturn(true);
		Mockito.when(plc.isBlockProductionStart()).thenReturn(true);

		startingState.deviceStatusChanged(new DeviceStatusEvent(DeviceStatus.STARTED, null));
		TestHelper.runAllTasks();
		Mockito.verify(conveyorController).setCurrentState(startedState);
	}

	@Test
	public void testDeviceStatusChangedDisconnected() {
		Mockito.when(startableDevice1.getName()).thenReturn("device1");
		startingState.deviceStatusChanged(new DeviceStatusEvent(DeviceStatus.DISCONNECTED, startableDevice1));
		Mockito.verify(conveyorController).setCurrentState(connectingState);
	}

}
