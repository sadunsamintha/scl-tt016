package com.sicpa.standard.sasscl.devices.camera;

import java.io.IOException;
import java.net.URISyntaxException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.sicpa.standard.camera.controller.internal.CognexCameraControllerImpl;
import com.sicpa.standard.camera.controller.model.CameraModel;
import com.sicpa.standard.camera.controller.model.ImageRetrievalType;
import com.sicpa.standard.camera.controller.model.SimulatorSendCodeMode;
import com.sicpa.standard.camera.parser.CameraCodeParser;
import com.sicpa.standard.client.common.utils.ConfigUtils;
import com.sicpa.standard.sasscl.devices.camera.jobconfig.parameters.provider.NoCameraJobParametersProvider;
import com.sicpa.standard.sasscl.devices.camera.transformer.IRoiCameraImageTransformer;

public class PhysicalCameraTest extends AbstractCameraAdaptorTest {

	public void test() {
	}

	private CameraModel cameraModel = null;

	@Before
	public void setUp() throws Exception {
		setupCameraModel();
		CognexCameraControllerImpl cimpl = new CognexCameraControllerImpl(this.cameraModel);
		cameraController = new CameraAdaptor(cimpl, Mockito.mock(IRoiCameraImageTransformer.class));
		cameraController.setCameraJobParametersProvider(new NoCameraJobParametersProvider());
		cimpl.addListener(cameraController);
		super.setup();
	}

	private void setupCameraModel() {
		this.cameraModel = new CameraModel();
		this.cameraModel.setId(1);
		this.cameraModel.setDriverName("CognexCameraDriver");
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
		this.cameraModel.setImageRetrievalType(ImageRetrievalType.NEVER);
		this.cameraModel.setLoadJob(false);
		this.cameraModel.setNoCodeReceivedIteration(1000);
		this.cameraModel.setSaveImage(false);
		this.cameraModel.setSimulatorMode(true);
		this.cameraModel.setSimulatorSendCodeInterval(200);
		this.cameraModel.setSimulatorSendCodeMode(SimulatorSendCodeMode.RANDOM);
		this.cameraModel.setCameraParserClass(CameraCodeParser.class);
	}

	/**
	 * test to get configuration from file
	 * 
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	@Test
	public void testLoadingConfiguration() throws IOException, URISyntaxException {

		CameraModel model = ConfigUtils.load("testresources/camera/camera_model.xml");

		new CameraAdaptor(new CognexCameraControllerImpl(model), Mockito.mock(IRoiCameraImageTransformer.class));
		
		// test a few attribute of the camera model
		Assert.assertEquals(1, model.getId());
		Assert.assertEquals("CognexCameraDriver", model.getDriverName());
		Assert.assertEquals("localhost", model.getIp());
		Assert.assertEquals(CameraCodeParser.class, model.getCameraParserClass());
	}
}
