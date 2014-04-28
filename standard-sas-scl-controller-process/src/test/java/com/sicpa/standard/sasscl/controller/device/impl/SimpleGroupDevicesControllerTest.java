package com.sicpa.standard.sasscl.controller.device.impl;

import static java.util.Arrays.asList;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.sicpa.standard.sasscl.controller.device.group.impl.SimpleGroupDevicesController;
import com.sicpa.standard.sasscl.devices.IDevice;
import com.sicpa.standard.sasscl.test.utils.TestHelper;

public class SimpleGroupDevicesControllerTest extends GroupDevicesControllerBaseHelper {

	@Before
	public void setUp() throws Exception {

		TestHelper.initExecutor();

		cameraDevice = new CameraDevice();
		printerDevice = new PrinterDevice();
		plcDevice = new PlcDevice();

		devicesController = new SimpleGroupDevicesController();

		for (IDevice device : asList(cameraDevice, printerDevice, plcDevice)) {

			((SimpleGroupDevicesController) devicesController).addDevice(device);
		}
	}

	@Test
	public void groupDevicesControllerStartStopTest() throws Exception {

		devicesController.start();

		TestHelper.runAllTasks();

		assertThat(cameraDevice.isConnected(), equalTo(true));
		assertThat(printerDevice.isConnected(), equalTo(true));
		assertThat(plcDevice.isConnected(), equalTo(true));

		devicesController.stop();

		TestHelper.runAllTasks();

		assertThat(cameraDevice.isConnected(), equalTo(false));
		assertThat(printerDevice.isConnected(), equalTo(false));
		assertThat(plcDevice.isConnected(), equalTo(false));

	}

}
