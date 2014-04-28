package com.sicpa.standard.sasscl.controller.hardware;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.controller.hardware.state.impl.StoppingState;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.devices.DeviceStatusEvent;
import com.sicpa.standard.sasscl.devices.IStartableDevice;
import com.sicpa.standard.sasscl.devices.plc.IPlcAdaptor;
import com.sicpa.standard.sasscl.test.utils.TestHelper;

public class StoppingStateTest {

	StoppingState stoppingState;
	IHardwareControllerState connectedState;
	IHardwareControllerState connectingState;

	HardwareController conveyorController;

	IPlcAdaptor plc;
	IStartableDevice startableDevice1;
	IStartableDevice startableDevice2;
	List<IStartableDevice> startableDevices;
	IDeviceErrorRepository deviceErrorRepository;

	@Before
	public void setup() {

		TestHelper.initExecutor();

		stoppingState = new StoppingState();
		connectedState = Mockito.mock(IHardwareControllerState.class);
		connectingState = Mockito.mock(IHardwareControllerState.class);

		deviceErrorRepository = Mockito.mock(IDeviceErrorRepository.class);

		conveyorController = Mockito.mock(HardwareController.class);

		plc = Mockito.mock(IPlcAdaptor.class);
		startableDevice1 = Mockito.mock(IStartableDevice.class);
		startableDevice2 = Mockito.mock(IStartableDevice.class);
		startableDevices = Arrays.asList(startableDevice1, startableDevice2);

		stoppingState.setConnectingState(connectingState);
		stoppingState.setConnectedState(connectedState);

		stoppingState.setPlc(plc);
		stoppingState.setSetter(conveyorController);
		stoppingState.setStartableDevices(startableDevices);
		stoppingState.setDeviceErrorRepository(deviceErrorRepository);

	}

	@Test
	public void testEnter() {
		EventBusService.register(new Object() {
			@SuppressWarnings("unused")
			@Subscribe
			public void handleEvent(HardwareControllerStatusEvent evt) {
				Assert.assertEquals(HardwareControllerStatus.STOPPING, evt.getStatus());
			}
		});
		stoppingState.enter();
		TestHelper.runAllTasks();
	}

	@Test
	public void testDeviceStatusChangedAllDeviceStopped() {

		Mockito.when(startableDevice1.getStatus()).thenReturn(DeviceStatus.STOPPED);
		Mockito.when(startableDevice2.getStatus()).thenReturn(DeviceStatus.STOPPED);
		Mockito.when(plc.getStatus()).thenReturn(DeviceStatus.STOPPED);

		Mockito.when(startableDevice1.isBlockProductionStart()).thenReturn(true);
		Mockito.when(startableDevice2.isBlockProductionStart()).thenReturn(true);
		Mockito.when(plc.isBlockProductionStart()).thenReturn(true);
		Mockito.when(deviceErrorRepository.isEmpty()).thenReturn(true);

		stoppingState.deviceStatusChanged(new DeviceStatusEvent(DeviceStatus.STOPPED, null));
		TestHelper.runAllTasks();
		Mockito.verify(conveyorController).setCurrentState(connectedState);
	}

	@Test
	public void testDeviceStatusChangedNotAllDeviceConnected() {

		Mockito.when(startableDevice1.getStatus()).thenReturn(DeviceStatus.DISCONNECTED);

		Mockito.when(startableDevice2.getStatus()).thenReturn(DeviceStatus.STOPPED);
		Mockito.when(plc.getStatus()).thenReturn(DeviceStatus.STOPPED);

		Mockito.when(startableDevice1.isBlockProductionStart()).thenReturn(true);
		Mockito.when(startableDevice2.isBlockProductionStart()).thenReturn(true);
		Mockito.when(plc.isBlockProductionStart()).thenReturn(true);

		stoppingState.deviceStatusChanged(new DeviceStatusEvent(DeviceStatus.STOPPED, null));
		TestHelper.runAllTasks();
		Mockito.verify(conveyorController).setCurrentState(connectingState);
	}

}
