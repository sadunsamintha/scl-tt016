package com.sicpa.standard.sasscl.devices.camera;

import com.sicpa.standard.camera.controller.ICameraController;
import com.sicpa.standard.sasscl.devices.DeviceException;
import com.sicpa.standard.sasscl.devices.camera.simulator.CameraSimulatorConfig;
import com.sicpa.standard.sasscl.devices.camera.simulator.CameraSimulatorController;
import com.sicpa.standard.sasscl.devices.camera.simulator.CodeGetMethod;
import com.sicpa.standard.sasscl.devices.camera.simulator.ICodeProvider;
import com.sicpa.standard.sasscl.devices.camera.transformer.IRoiCameraImageTransformer;
import com.sicpa.standard.sasscl.model.Code;
import com.sicpa.standard.sasscl.model.ProductionMode;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;

public class CameraSimulatorTest extends AbstractCameraAdaptorTest {

	CameraSimulatorConfig camConfig;
	CameraSimulatorController simulator;

	@Before
	public void setUp() throws Exception {
		camConfig = new CameraSimulatorConfig();
		camConfig.setCodeGetMethod(CodeGetMethod.generated);
		camConfig.setReadCodeInterval(5);
		camConfig.setPercentageBadCode(25);
		simulator = new CameraSimulatorController(camConfig);
		simulator.setProductionParameters(productionParameters);
		cameraController = new CameraAdaptor(simulator, Mockito.mock(IRoiCameraImageTransformer.class));
		simulator.addListener(cameraController);
		super.setup();
	}

	/**
	 * Dummy listener class to simulate request code method
	 * 
	 * @author CWong
	 * 
	 */
	static class CodeProvider implements ICodeProvider {

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
		camConfig.setCodeGetMethod(CodeGetMethod.requested);
		simulator.setCodeProvider(new CodeProvider());
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

		camConfig.setCodeGetMethod(CodeGetMethod.file);
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

		camConfig.setCodeGetMethod(CodeGetMethod.generated);
		cameraController.connect();
		cameraController.start();
		waitToGetSomeCodes();
		cameraController.stop();
		Assert.assertTrue(good + bad > 0);
	}

	@Test
	public void testBad() throws DeviceException {
		camConfig.setCodeGetMethod(CodeGetMethod.generated);
		camConfig.setPercentageBadCode(25);
		cameraController.connect();
		cameraController.start();
		waitToGetBadCodes();
		cameraController.stop();
		Assert.assertTrue(bad != 0);
	}

	@Test
	public void testExport() throws InterruptedException, DeviceException {
		productionParameters.setProductionMode(ProductionMode.EXPORT);
		simulator = new CameraSimulatorController(camConfig);
		simulator.setProductionParameters(productionParameters);
		cameraController = new CameraAdaptor(simulator, Mockito.mock(IRoiCameraImageTransformer.class));
		cameraController.connect();
		cameraController.start();
		waitToGetBadCodes();

		cameraController.stop();
		Assert.assertTrue(good + bad > 0);

		Assert.assertTrue(cameraController.getController() instanceof ICameraController<?>);

	}

	@Test
	public void testCameraEvent() {
		Code code = new Code("E");
		CameraGoodCodeEvent event = new CameraGoodCodeEvent(code);
		Assert.assertSame(code, event.getCode());
	}
}
