package com.sicpa.standard.sasscl.devices.camera.d900;

import com.sicpa.standard.camera.d900.controller.ID900CameraController;
import com.sicpa.standard.sasscl.devices.DeviceException;
import com.sicpa.standard.sasscl.devices.camera.d900.simulator.D900CameraSimulatorConfig;
import com.sicpa.standard.sasscl.devices.camera.d900.simulator.D900CameraSimulatorController;
import com.sicpa.standard.sasscl.devices.camera.d900.simulator.D900CodeGetMethod;
import com.sicpa.standard.sasscl.devices.d900.D900CameraGoodCodeEvent;
import com.sicpa.standard.sasscl.devices.d900.simulator.ID900CodeProvider;
import com.sicpa.standard.sasscl.model.Code;
import com.sicpa.standard.sasscl.model.ProductionMode;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class D900CameraSimulatorTest extends D900AbstractCameraAdaptorTest {

	D900CameraSimulatorConfig camConfig;
	D900CameraSimulatorController simulator;

	@Before
	public void setUp() throws Exception {
		camConfig = new D900CameraSimulatorConfig();
		camConfig.setCodeGetMethod(D900CodeGetMethod.generated);
		camConfig.setReadCodeInterval(5);
		camConfig.setPercentageBadCode(25);
		simulator = new D900CameraSimulatorController(camConfig);
		simulator.setProductionParameters(productionParameters);
		cameraController = new D900CameraAdaptor(simulator);
		simulator.addListener(cameraController);
		super.setup();
	}

	/**
	 * Dummy listener class to simulate request code method
	 * 
	 * @author CWong
	 * 
	 */
	static class D900CodeProvider implements ID900CodeProvider {

		@Override
		public String requestCode() {
			return "0123456789";
		}
	}

	/**
	 * Calculate how many occurrences of the specified thread
	 * 
	 * @param name
	 *            Thread name
	 * @return
	 */
	int threadCountByName(final String name) {
		ThreadGroup tg = Thread.currentThread().getThreadGroup();
		tg.list();
		int threadCount = 0;
		int count = tg.activeCount();
		Thread[] tArray = new Thread[count];
		tg.enumerate(tArray);
		for (int i = 0; i < tArray.length; i++) {
			if (tArray[i].getName().equalsIgnoreCase(name)) {
				threadCount++;
			}
		}

		return threadCount;
	}

	/**
	 * Test reading codes from other components
	 * 
	 * @throws InterruptedException
	 * @throws DeviceException
	 */
	@Test
	public void cameraSimulatorControllerAskCodeTest() throws InterruptedException, DeviceException {
		camConfig.setCodeGetMethod(D900CodeGetMethod.requested);
		simulator.setCodeProvider(new D900CodeProvider());
		cameraController.connect();
		cameraController.start();
		waitToGetAnyCodes();
		cameraController.stop();
		Assert.assertTrue(good + bad > 0);
	}

	/**
	 * Test reading codes from file
	 * 
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws DeviceException
	 */
	@Test
	public void cameraSimulatorControllerReadFileTest() throws InterruptedException, IOException, DeviceException {

		camConfig.setCodeGetMethod(D900CodeGetMethod.file);
		String path = "testresources/camera/test.data";
		camConfig.setDataFilePath(path);

		// create the file that will contains codes to read
		File f = new File(path);
		String codes = "132,465,798\n132,465,798,123,465,798,132,465,798\n132,465,798\n132,465,798\n";
		FileUtils.writeStringToFile(f, codes);

		cameraController.connect();
		cameraController.start();
		waitToGetAnyCodes();
		cameraController.stop();

		Assert.assertTrue(good + bad > 0);
	}

	/**
	 * Test reading codes from random generated code
	 * 
	 * @throws InterruptedException
	 * @throws DeviceException
	 */
	@Test
	public void cameraSimulatorControllerRandomCodeTest() throws InterruptedException, DeviceException {

		camConfig.setCodeGetMethod(D900CodeGetMethod.generated);
		cameraController.connect();
		cameraController.start();
		waitToGetSomeCodes();
		cameraController.stop();
		Assert.assertTrue(good + bad > 0);
	}

	@Test
	public void testBad() throws DeviceException {
		camConfig.setCodeGetMethod(D900CodeGetMethod.generated);
		camConfig.setPercentageBadCode(25);
		cameraController.connect();
		cameraController.start();
		waitToGetBadCodes();
		cameraController.stop();
		Assert.assertTrue(bad != 0);
	}


	@Test
	public void testCameraEvent() {
		String code = "E";
		D900CameraGoodCodeEvent event = new D900CameraGoodCodeEvent(code);
		Assert.assertSame(code, event.getCode());
	}
}
