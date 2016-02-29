package com.sicpa.standard.sasscl.coding;

import java.io.File;
import java.util.Arrays;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.ioc.BeanProvider;
import com.sicpa.standard.client.common.utils.TaskTimeoutExecutor;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.sasscl.AbstractFunctionnalTest;
import com.sicpa.standard.sasscl.common.storage.FileStorage;
import com.sicpa.standard.sasscl.common.storage.IStorage;
import com.sicpa.standard.sasscl.devices.camera.CameraGoodCodeEvent;
import com.sicpa.standard.sasscl.devices.camera.simulator.CameraAdaptorSimulator;
import com.sicpa.standard.sasscl.devices.camera.simulator.CameraSimulatorController;
import com.sicpa.standard.sasscl.devices.camera.simulator.CodeGetMethod;
import com.sicpa.standard.sasscl.devices.remote.RemoteServerException;
import com.sicpa.standard.sasscl.devices.remote.simulator.ISimulatorGetEncoder;
import com.sicpa.standard.sasscl.ioc.BeansName;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.sicpadata.CryptographyException;
import com.sicpa.standard.sasscl.sicpadata.generator.IEncoder;
import com.sicpa.standard.sasscl.sicpadata.generator.impl.CodeListEncoder;

public class SaveFinishedEncoderTestSCL extends AbstractFunctionnalTest {

	@Override
	protected ProductionMode getProductionMode() {
		return SCL_MODE;
	}

	volatile boolean codeReceived = false;

	public void test() throws Exception {
		init();

		EventBusService.register(this);

		generateSmallEncoder();
		setProductionParameter();

		checkApplicationStatusCONNECTED();

		startProduction();
		checkApplicationStatusRUNNING();

		waitForACode();

		stopProduction();
		checkApplicationStatusCONNECTED();

		checkFinishedEncoderGenerated();
		exit();
	}

	private void waitForACode() throws Exception {
		TaskTimeoutExecutor.execute(new Runnable() {
			@Override
			public void run() {
				while (!codeReceived) {
					ThreadUtils.sleepQuietly(10);
				}
			}
		}, 30);
	}

	@Subscribe
	public void handleCode(CameraGoodCodeEvent evt) {
		codeReceived = true;
	}

	private void generateSmallEncoder() throws RemoteServerException, CryptographyException {
		IStorage storage = BeanProvider.getBean(BeansName.STORAGE);
		IEncoder encoder = ((ISimulatorGetEncoder) remoteServer).getEncoder(new CodeType(1));
		CodeListEncoder codeListEncoder = new CodeListEncoder(encoder.getId(), 1, encoder.getYear(),
				encoder.getSubsystemId(), Arrays.asList(encoder.getEncryptedCodes(1).get(0)), 1);
		storage.saveCurrentEncoder(codeListEncoder);
	}

	private void checkFinishedEncoderGenerated() {
		assertTrue(new File(PROFILE_FOLDER + "/internalSimulator/" + FileStorage.FOLDER_ENCODER_FINISHED_PENDING)
				.listFiles().length > 0);
	}

	protected void configureDevices() {
		CameraAdaptorSimulator cameraDevice = (CameraAdaptorSimulator) devicesMap.get("qc_1");
		camera = (CameraSimulatorController) cameraDevice.getSimulatorController();
		cameraModel = camera.getCameraModel();
		cameraModel.setCodeGetMethod(CodeGetMethod.requested);
	}
}
