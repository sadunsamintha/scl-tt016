/**
 * Author	: YYang
 * Date		: Aug 4, 2010
 *
 * Copyright (c) 2010 SICPA Security Solutions, all rights reserved.
 *
 */
package com.sicpa.standard.sasscl.devices.camera;

import static org.junit.Assert.assertEquals;
import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.sasscl.devices.DeviceException;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.devices.DeviceStatusEvent;
import com.sicpa.standard.sasscl.devices.IDeviceStatusListener;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.ProductionParameters;

/**
 * Defines unit tests for CameraController. Here we enable the simulator mode in standard camera component to bring up
 * the server to mock the real camera behavior.Random code will be sent by the camera simulator server to mimic the real
 * camera sending code to the camera controller listener
 * 
 */
@Ignore
public class AbstractCameraAdaptorTest {

	protected static volatile int good = 0;
	protected static volatile int bad = 0;
	protected ProductionParameters productionParameters = new ProductionParameters(ProductionMode.STANDARD, null, null);

	static class CodeListener {
		@Subscribe
		public void receiveCameraCode(final CameraGoodCodeEvent evt) {
			good++;
			System.out.println("good");
		}

		@Subscribe
		public void receiveCameraCodeError(final CameraBadCodeEvent evt) {
			bad++;
			System.out.println("bad");
		}

	}

	protected CameraAdaptor cameraController = null;

	protected CodeListener codeListener = new CodeListener();

	protected IDeviceStatusListener deviceStatusListener = Mockito.mock(IDeviceStatusListener.class);

	protected void setup() {
		good = 0;
		bad = 0;
		EventBusService.register(codeListener);
		cameraController.addDeviceStatusListener(deviceStatusListener);
	}

	protected void waitCameraConnected() {
		long firstTime = System.currentTimeMillis();
		while (!cameraController.isConnected()) {
			if (System.currentTimeMillis() - firstTime > 30000) {
				Assert.fail("timeout connecting to camera");
			}
			ThreadUtils.sleepQuietly(1);
		}
	}

	protected void waitToGetSomeCodes() {
		long firstTime = System.currentTimeMillis();
		while (good <= 0 || bad <= 0) {
			if (System.currentTimeMillis() - firstTime > 30000) {
				Assert.fail("timeout getting codes");
			}
			ThreadUtils.sleepQuietly(1);
		}
	}

	protected void waitToGetBadCodes() {
		long firstTime = System.currentTimeMillis();
		while (bad <= 0) {
			if (System.currentTimeMillis() - firstTime > 30000) {
				Assert.fail("timeout getting codes");
			}
			ThreadUtils.sleepQuietly(1);
		}
	}

	protected void waitToGetAnyCodes() {
		long firstTime = System.currentTimeMillis();
		while (good <= 0 && bad <= 0) {
			if (System.currentTimeMillis() - firstTime > 30000) {
				Assert.fail("timeout getting codes");
			}
			ThreadUtils.sleepQuietly(1);
		}
	}

	@Test
	public void testCameraInitialization() throws DeviceException {

		cameraController.connect();

		waitCameraConnected();

		// connected event should be received in device status listener
		ArgumentCaptor<DeviceStatusEvent> deviceStatusArgument = ArgumentCaptor.forClass(DeviceStatusEvent.class);

		Mockito.verify(deviceStatusListener, Mockito.atLeastOnce()).deviceStatusChanged(deviceStatusArgument.capture());

		assertEquals(DeviceStatus.CONNECTED, deviceStatusArgument.getValue().getStatus());

		// perform clean up for next test
		cameraController.disconnect();

		Mockito.verify(deviceStatusListener, Mockito.atLeastOnce()).deviceStatusChanged(deviceStatusArgument.capture());

		// should get disconnected status after the disconnect() method
		assertEquals(DeviceStatus.DISCONNECTED, deviceStatusArgument.getValue().getStatus());

	}

	/**
	 * test camera controller is able to get the code when the start reading method is called
	 * 
	 * @throws DeviceException
	 */
	@Test
	@Ignore
	public void testCameraGetCode() throws DeviceException {

		cameraController.connect();

		waitCameraConnected();

		// call start method to start receiving code from the camera
		cameraController.start();

		waitToGetSomeCodes();

		// verify camera listener get some good/bad code from camera controller

		Assert.assertTrue(good > 0);
		Assert.assertTrue(bad > 0);

		cameraController.stop();

		good = 0;
		bad = 0;

		ThreadUtils.sleepQuietly(1000);

		Assert.assertTrue(good == 0);
		Assert.assertTrue(bad == 0);
		cameraController.disconnect();

	}
}
