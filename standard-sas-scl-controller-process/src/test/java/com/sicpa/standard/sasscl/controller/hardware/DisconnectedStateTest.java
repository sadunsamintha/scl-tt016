package com.sicpa.standard.sasscl.controller.hardware;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.controller.hardware.state.impl.DisconnectedState;
import com.sicpa.standard.sasscl.devices.IStartableDevice;
import com.sicpa.standard.sasscl.test.utils.TestHelper;

public class DisconnectedStateTest {

	private DisconnectedState disconnectedState;
	private IHardwareControllerState connectingState;
	private HardwareController conveyorController;

	@Before
	public void setup() {
		TestHelper.initExecutor();
		disconnectedState = new DisconnectedState();
		connectingState = Mockito.mock(IHardwareControllerState.class);
		conveyorController = Mockito.mock(HardwareController.class);

		disconnectedState.setSetter(conveyorController);
		disconnectedState.setConnectingState(connectingState);
		disconnectedState.setStartableDevices(new ArrayList<IStartableDevice>());
	}

	@Test
	public void testEnter() {

		EventBusService.register(new Object() {
			@SuppressWarnings("unused")
			@Subscribe
			public void handleEvent(HardwareControllerStatusEvent evt) {
				Assert.assertEquals(HardwareControllerStatus.DISCONNECTED, evt.getStatus());
			}
		});

		disconnectedState.enter();
		TestHelper.runAllTasks();
	}

	@Test
	public void testConnect() {
		disconnectedState.connect();
		TestHelper.runAllTasks();
		Mockito.verify(conveyorController).setCurrentState(connectingState);
	}

}
