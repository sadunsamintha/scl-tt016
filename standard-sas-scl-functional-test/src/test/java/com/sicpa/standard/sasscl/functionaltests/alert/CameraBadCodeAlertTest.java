package com.sicpa.standard.sasscl.functionaltests.alert;

import com.sicpa.standard.client.common.ioc.BeanProvider;
import com.sicpa.standard.sasscl.AbstractFunctionnalTest;
import com.sicpa.standard.sasscl.devices.camera.alert.CameraCountAlertTask;
import com.sicpa.standard.sasscl.ioc.BeansName;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.model.ProductionMode;

public class CameraBadCodeAlertTest extends AbstractFunctionnalTest {

	private CameraCountAlertTask cameraCountAlertTask;

	public void test() throws Exception {

		init();

		setProductionParameter();

		checkApplicationStatusCONNECTED();

		startProduction();
		checkApplicationStatusRUNNING();

		generateCameraCodes();

		runAllTasks();
		cameraCountAlertTask.checkForMessage();
		checkApplicationStatusCONNECTED();
		checkWarningMessage(MessageEventKey.Alert.TOO_MUCH_CAMERA_ERROR);
		exit();
	}

	@Override
	protected ProductionMode getProductionMode() {
		return SCL_MODE;
	}

	@Override
	public void init() {
		super.init();

		cameraCountAlertTask = BeanProvider.getBean(BeansName.ALERT_CAMERA_COUNT);
		cameraCountAlertTask.getModel().setMaxUnreadCount(3);
		cameraCountAlertTask.getModel().setSampleSize(10);
		cameraCountAlertTask.getModel().setDelayInSec(99999);
	}

	public void generateCameraCodes() throws Exception {

		camera.fireGoodCode(generateACodeFromEncoder());
		camera.fireGoodCode(generateACodeFromEncoder());
		camera.fireBadCode("");
		camera.fireGoodCode(generateACodeFromEncoder());
		camera.fireBadCode("");
		camera.fireGoodCode(generateACodeFromEncoder());
		camera.fireGoodCode(generateACodeFromEncoder());
		camera.fireGoodCode(generateACodeFromEncoder());
		camera.fireBadCode("");
		camera.fireGoodCode(generateACodeFromEncoder());
		camera.fireBadCode("");
	}
}
