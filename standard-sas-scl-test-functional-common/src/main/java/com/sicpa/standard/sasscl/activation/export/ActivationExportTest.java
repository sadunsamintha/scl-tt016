package com.sicpa.standard.sasscl.activation.export;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.AbstractFunctionnalTest;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.test.utils.TestHelper;

public abstract class ActivationExportTest extends AbstractFunctionnalTest {

	public void test() {
		init();
		EventBusService.register(this);
		setProductionParameter(1, 1, ProductionMode.EXPORT);

		TestHelper.runAllTasks();
		checkApplicationStatusCONNECTED();

		startProduction();
		checkApplicationStatusRUNNING();

		generateCameraCodes(20);
		checkApplicationStatusCONNECTED();

		exit();
		checkDataSentToRemoteServer();
	}

	public void generateCameraCodes(int good) {
		for (int i = 0; i < good; i++) {
			camera.fireBadCode("");
			dataGenerated.add("EXPORT" + null + "SKU#1");
		}
		String code = "100" + 1;
		camera.fireGoodCode(code);
		dataGenerated.add("UNREAD" + code + "SKU#1");
	}

}
