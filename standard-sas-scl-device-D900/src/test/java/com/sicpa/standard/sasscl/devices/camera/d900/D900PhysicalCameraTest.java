package com.sicpa.standard.sasscl.devices.camera.d900;

import java.io.IOException;
import java.net.URISyntaxException;

import com.sicpa.standard.camera.d900.controller.model.D900CameraModel;
import com.sicpa.standard.camera.d900.controller.model.D900ImageRetrievalType;
import com.sicpa.standard.camera.d900.controller.model.D900SimulatorSendCodeMode;
import com.sicpa.standard.camera.d900.parser.D900CameraCodeParser;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.sicpa.standard.camera.d900.controller.internal.D900CameraControllerImpl;
import com.sicpa.standard.client.common.utils.ConfigUtils;

public class D900PhysicalCameraTest extends D900AbstractCameraAdaptorTest {

	public void test() {
	}

	private D900CameraModel cameraModel = null;

	@Before
	public void setUp() throws Exception {
		setupCameraModel();
		D900CameraControllerImpl cimpl = new D900CameraControllerImpl(this.cameraModel);
		cameraController = new D900CameraAdaptor(cimpl);
		cimpl.addListener(cameraController);
		super.setup();
	}

	private void setupCameraModel() {
		this.cameraModel = new D900CameraModel();
		this.cameraModel.setId(1);
		this.cameraModel.setDriverName("D900CameraDriver");
		this.cameraModel.setPort(11444);
		this.cameraModel.setIp("localhost");
		this.cameraModel.setConnectionTimeout(500);
		this.cameraModel.setLifeCheckInterval(500);
		this.cameraModel.setTelnetPort(1122);
		this.cameraModel.setTelnetUser("admin");
		this.cameraModel.setTelnetPassword("");
		this.cameraModel.setFTPPort(21);
		this.cameraModel.setFTPUser("admin");
		this.cameraModel.setFTPPassword("");
		this.cameraModel.setImageFileName("image.jpg");
		this.cameraModel.setImageRetrievalType(D900ImageRetrievalType.NEVER);
		this.cameraModel.setLoadJob(false);
		this.cameraModel.setNoCodeReceivedIteration(1000);
		this.cameraModel.setSaveImage(false);
		this.cameraModel.setSimulatorMode(true);
		this.cameraModel.setSimulatorSendCodeInterval(200);
		this.cameraModel.setSimulatorSendCodeMode(D900SimulatorSendCodeMode.RANDOM);
		this.cameraModel.setCameraParserClass(D900CameraCodeParser.class);
	}

	/**
	 * test to get configuration from file
	 * 
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	@Test
	public void testLoadingConfiguration() throws IOException, URISyntaxException {

		D900CameraModel model = ConfigUtils.load("testresources/camera/D900_model.xml");

		new D900CameraAdaptor(new D900CameraControllerImpl(model));
		
		// test a few attribute of the camera model
		Assert.assertEquals(1, model.getId());
		Assert.assertEquals("D900CameraDriver", model.getDriverName());
		Assert.assertEquals("localhost", model.getIp());
		Assert.assertEquals(D900CameraCodeParser.class, model.getCameraParserClass());
	}
}
