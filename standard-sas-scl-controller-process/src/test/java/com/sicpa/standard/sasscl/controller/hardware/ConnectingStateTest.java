package com.sicpa.standard.sasscl.controller.hardware;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.controller.hardware.state.impl.ConnectingState;
import com.sicpa.standard.sasscl.devices.DeviceException;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.devices.DeviceStatusEvent;
import com.sicpa.standard.sasscl.devices.IStartableDevice;
import com.sicpa.standard.sasscl.devices.plc.IPlcAdaptor;
import com.sicpa.standard.sasscl.test.utils.TestHelper;

public class ConnectingStateTest {

	private ConnectingState connectingState;

	private IHardwareControllerState connectedState;
	private IHardwareControllerState disconnectingState;

	private HardwareController conveyorController;
	private IPlcAdaptor plc;
	private IStartableDevice startableDevice1;
	private IStartableDevice startableDevice2;
	private List<IStartableDevice> startableDevices;

	@Before
	public void setup() {
		TestHelper.initExecutor();
		connectingState = new ConnectingState();

		connectedState = mock(IHardwareControllerState.class);
		disconnectingState = mock(IHardwareControllerState.class);
		conveyorController = mock(HardwareController.class);

		plc = mock(IPlcAdaptor.class);

		startableDevice1 = mock(IStartableDevice.class);
		startableDevice2 = mock(IStartableDevice.class);
		startableDevices = Arrays.asList(startableDevice1, startableDevice2);
		
		Mockito.when(startableDevice1.isBlockProductionStart()).thenReturn(true);
		Mockito.when(startableDevice2.isBlockProductionStart()).thenReturn(true);

		connectingState.setPlc(plc);
		connectingState.setStartableDevices(startableDevices);
		connectingState.setDisconnectingState(disconnectingState);

		connectingState.setSetter(conveyorController);
		connectingState.setConnectedState(connectedState);
		connectingState.setDeviceErrorRepository(new DeviceErrorRepository());
	}

	@Test
	public void testEnter() throws DeviceException {

		when(startableDevice1.getStatus()).thenReturn(DeviceStatus.DISCONNECTED);
		when(startableDevice2.getStatus()).thenReturn(DeviceStatus.DISCONNECTED);
		when(plc.getStatus()).thenReturn(DeviceStatus.DISCONNECTED);

		EventBusService.register(new Object() {
			@SuppressWarnings("unused")
			@Subscribe
			public void handleEvent(HardwareControllerStatusEvent evt) {
				Assert.assertEquals(HardwareControllerStatus.CONNECTING, evt.getStatus());
				Assert.assertEquals(3, evt.getErrors().size());
			}
		});

		connectingState.enter();
		TestHelper.runAllTasks();

		verify(startableDevice1).connect();
		verify(startableDevice2).connect();
		verify(plc).connect();
	}

	@Test
	public void testDeviceStatusChangedAllConnected() {

		when(startableDevice1.getStatus()).thenReturn(DeviceStatus.CONNECTED);
		when(startableDevice2.getStatus()).thenReturn(DeviceStatus.CONNECTED);
		when(plc.getStatus()).thenReturn(DeviceStatus.CONNECTED);
		when(startableDevice1.isBlockProductionStart()).thenReturn(true);
		when(startableDevice2.isBlockProductionStart()).thenReturn(true);

		connectingState.deviceStatusChanged(new DeviceStatusEvent(DeviceStatus.CONNECTED, null));
		TestHelper.runAllTasks();
		verify(conveyorController).setCurrentState(connectedState);
	}

	@Test
	public void testDeviceStatusChangedNotAllConnected() {
		when(startableDevice1.getStatus()).thenReturn(DeviceStatus.CONNECTED);
		when(startableDevice2.getStatus()).thenReturn(DeviceStatus.CONNECTED);
		when(plc.getStatus()).thenReturn(DeviceStatus.DISCONNECTED);
		when(startableDevice1.isBlockProductionStart()).thenReturn(true);
		when(startableDevice2.isBlockProductionStart()).thenReturn(true);

		connectingState.deviceStatusChanged(new DeviceStatusEvent(DeviceStatus.CONNECTED, null));
		TestHelper.runAllTasks();

		verify(conveyorController, Mockito.times(0)).setCurrentState(connectedState);
	}

	@Test
	public void testDisconnect() {
		connectingState.disconnect();
		TestHelper.runAllTasks();
		Mockito.verify(conveyorController).setCurrentState(disconnectingState);
	}

}
