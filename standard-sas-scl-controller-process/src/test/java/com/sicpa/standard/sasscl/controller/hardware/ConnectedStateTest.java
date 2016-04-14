package com.sicpa.standard.sasscl.controller.hardware;

import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.controller.hardware.state.impl.ConnectedState;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.devices.DeviceStatusEvent;
import com.sicpa.standard.sasscl.devices.IStartableDevice;
import com.sicpa.standard.sasscl.devices.plc.IPlcAdaptor;
import com.sicpa.standard.sasscl.test.utils.TestHelper;

public class ConnectedStateTest {

	private ConnectedState connectedState;

	IHardwareControllerState connectingState;
	IHardwareControllerState disconnectingState;
	IHardwareControllerState startingState;

	private HardwareController conveyorController;
	private IPlcAdaptor plc;
	private IStartableDevice startableDevice1;
	private IStartableDevice startableDevice2;
	private List<IStartableDevice> startableDevices;

	@Before
	public void setup() {

		TestHelper.initExecutor();

		connectedState = new ConnectedState();

		connectingState = mock(IHardwareControllerState.class);
		disconnectingState = mock(IHardwareControllerState.class);
		startingState = mock(IHardwareControllerState.class);

		conveyorController = mock(HardwareController.class);

		plc = mock(IPlcAdaptor.class);
		startableDevice1 = mock(IStartableDevice.class);
		startableDevice2 = mock(IStartableDevice.class);
		startableDevices = Arrays.asList(startableDevice1, startableDevice2);

		connectedState.setPlc(plc);
		connectedState.setStartableDevices(startableDevices);
		connectedState.setDisconnectingState(disconnectingState);

		connectedState.setSetter(conveyorController);
		connectedState.setConnectingState(connectingState);
		connectedState.setDisconnectingState(disconnectingState);
		connectedState.setStartingState(startingState);

		connectedState.setSetter(conveyorController);
	}

	@Test
	public void testEnter() {
		EventBusService.register(new Object() {
			@Subscribe
			public void handleEvent(HardwareControllerStatusEvent evt) {
				Assert.assertEquals(HardwareControllerStatus.CONNECTED, evt.getStatus());
			}
		});
		connectedState.enter();
		TestHelper.runAllTasks();
	}

	@Test
	public void testStart() {
		connectedState.start();
		TestHelper.runAllTasks();
		Mockito.verify(conveyorController).setCurrentState(startingState);
	}

	@Test
	public void testDisconnect() {
		connectedState.disconnect();
		TestHelper.runAllTasks();
		Mockito.verify(conveyorController).setCurrentState(disconnectingState);
	}

	@Test
	public void testDeviceStatusChangeded() {
		Mockito.when(startableDevice1.getName()).thenReturn("PLC");
		connectedState.deviceStatusChanged(new DeviceStatusEvent(DeviceStatus.DISCONNECTED, startableDevice1));
		TestHelper.runAllTasks();
		Mockito.verify(conveyorController).setCurrentState(connectingState);
	}
}
