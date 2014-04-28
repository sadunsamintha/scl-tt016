package com.sicpa.standard.sasscl.alert;

import com.sicpa.standard.client.common.ioc.BeanProvider;
import com.sicpa.standard.sasscl.AbstractFunctionnalTest;
import com.sicpa.standard.sasscl.config.GlobalBean;
import com.sicpa.standard.sasscl.devices.camera.alert.CameraCountAlertTask;
import com.sicpa.standard.sasscl.ioc.BeansName;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.model.ProductionMode;

public abstract class CameraBadCodeAlertTest extends AbstractFunctionnalTest {

	protected CameraCountAlertTask cameraCountAlertTask;

	public void test() throws Exception {

		init();

		setProductionParameter(1, 1, ProductionMode.STANDARD);

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
	public void init() {
		super.init();

		GlobalBean config = BeanProvider.getBean(BeansName.GLOBAL_CONFIG);
		config.getAlertModel().getCameraCountModel().setMaxUnreadCount(3);
		config.getAlertModel().getCameraCountModel().setSampleSize(10);
		config.getAlertModel().getCameraCountModel().setDelayInSec(99999);

		cameraCountAlertTask = BeanProvider.getBean(BeansName.ALERT_CAMERA_COUNT);
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
