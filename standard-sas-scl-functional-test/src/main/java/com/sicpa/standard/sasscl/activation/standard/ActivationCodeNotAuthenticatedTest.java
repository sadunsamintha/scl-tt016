package com.sicpa.standard.sasscl.activation.standard;

import com.sicpa.standard.sasscl.AbstractFunctionnalTest;
import com.sicpa.standard.sasscl.messages.MessageEventKey;

public abstract class ActivationCodeNotAuthenticatedTest extends AbstractFunctionnalTest {

	public void test() {

		init();

		setProductionParameter();
		runAllTasks();

		checkApplicationStatusCONNECTED();

		startProduction();

		runAllTasks();

		checkApplicationStatusRUNNING();

		generateCameraCode();

		checkApplicationStatusCONNECTED();
		checkWarningMessage(MessageEventKey.Activation.EXCEPTION_NOT_AUTHENTICATED);
		exit();

		checkDataSentToRemoteServer();
	}

	public void generateCameraCode() {
		String code = "100" + 0001;
		camera.fireGoodCode(code);
		dataGenerated.add("NOT_AUTHENTICATED" + code + "SKU#1");
	}

}
