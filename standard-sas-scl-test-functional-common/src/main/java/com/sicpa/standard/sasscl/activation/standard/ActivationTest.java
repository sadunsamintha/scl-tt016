package com.sicpa.standard.sasscl.activation.standard;

import com.sicpa.standard.sasscl.AbstractFunctionnalTest;
import com.sicpa.standard.sasscl.model.ProductionMode;

public abstract class ActivationTest extends AbstractFunctionnalTest {

	public void test() throws Exception {

		init();

		setProductionParameter(1, 1, ProductionMode.STANDARD);
		runAllTasks();
		checkApplicationStatusCONNECTED();

		startProduction();
		checkApplicationStatusRUNNING();

		generateCameraCodes(20, 1);
		checkApplicationStatusRUNNING();

		stopProduction();
		checkApplicationStatusCONNECTED();
		checkStatistics(20, 1);

		exit();

		checkDataSentToRemoteServer();
	}

	public void generateCameraCodes(int good, int bad) throws Exception {
		for (int i = 0; i < good; i++) {
			String code = generateACodeFromEncoder();
			camera.fireGoodCode(code);
			dataGenerated.add("AUTHENTICATED" + code + "SKU#1");
		}

		for (int i = 0; i < bad; i++) {
			String code = "B00" + i;
			camera.fireBadCode(code);
			dataGenerated.add("UNREAD" + code + "SKU#1");
		}
	}

}
