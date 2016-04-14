package com.sicpa.standard.sasscl.controller.hardware;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.controller.hardware.state.impl.DisconnectingState;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.devices.DeviceStatusEvent;
import com.sicpa.standard.sasscl.devices.IStartableDevice;
import com.sicpa.standard.sasscl.devices.plc.IPlcAdaptor;
import com.sicpa.standard.sasscl.test.utils.TestHelper;

public class DisconnectingStateTest {

	private DisconnectingState disconnectingState;
	private IHardwareControllerState disconnectedState;

	private HardwareController conveyorController;

	private IPlcAdaptor plc;
	private IStartableDevice startableDevice1;
	private IStartableDevice startableDevice2;
	private List<IStartableDevice> startableDevices;

	@Before
	public void setup() {

		TestHelper.initExecutor();

		disconnectingState = new DisconnectingState();
		disconnectedState = Mockito.mock(IHardwareControllerState.class);
		conveyorController = Mockito.mock(HardwareController.class);

		plc = Mockito.mock(IPlcAdaptor.class);
		startableDevice1 = Mockito.mock(IStartableDevice.class);
		startableDevice2 = Mockito.mock(IStartableDevice.class);
		startableDevices = Arrays.asList(startableDevice1, startableDevice2);

		disconnectingState.setPlc(plc);
		disconnectingState.setStartableDevices(startableDevices);
		disconnectingState.setSetter(conveyorController);
		disconnectingState.setDisconnectedState(disconnectedState);
	}

	@Test
	public void testEnter() {
		EventBusService.register(new Object() {
			@Subscribe
			public void handleEvent(HardwareControllerStatusEvent evt) {
				Assert.assertEquals(HardwareControllerStatus.DISCONNECTING, evt.getStatus());
			}
		});
		disconnectingState.enter();
		TestHelper.runAllTasks();
	}

	@Test
	public void testDeviceStatusChanged() {
		Mockito.when(startableDevice1.getStatus()).thenReturn(DeviceStatus.DISCONNECTED);
		Mockito.when(startableDevice2.getStatus()).thenReturn(DeviceStatus.DISCONNECTED);
		Mockito.when(plc.getStatus()).thenReturn(DeviceStatus.DISCONNECTED);
		disconnectingState.deviceStatusChanged(new DeviceStatusEvent(DeviceStatus.DISCONNECTED, null));
		TestHelper.runAllTasks();
		Mockito.verify(conveyorController).setCurrentState(disconnectedState);
	}

	@Test
	public void testDeviceStatusChangedNotAllDisconnected() {
		Mockito.when(startableDevice1.getStatus()).thenReturn(DeviceStatus.DISCONNECTED);
		Mockito.when(startableDevice2.getStatus()).thenReturn(DeviceStatus.DISCONNECTED);
		Mockito.when(plc.getStatus()).thenReturn(DeviceStatus.DISCONNECTING);
		disconnectingState.deviceStatusChanged(new DeviceStatusEvent(DeviceStatus.DISCONNECTED, null));
		TestHelper.runAllTasks();
		Mockito.verify(conveyorController, Mockito.times(0)).setCurrentState(disconnectedState);
	}

}
