package com.sicpa.standard.sasscl.activation.standard;

import com.sicpa.standard.sasscl.AbstractFunctionnalTest;

public abstract class ActivationTest extends AbstractFunctionnalTest {

	public void test() throws Exception {

		init();

		setProductionParameter();
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
			String code = trigGood();
			System.out.println("read :" + code);
			dataGenerated.add("AUTHENTICATED" + code + "SKU#1");
			runAllTasks();
		}

		for (int i = 0; i < bad; i++) {
			String code = trigBad();
			System.out.println("unread :" + code);
			if (printer == null) {
				dataGenerated.add("UNREAD" + code + "SKU#1");
			} else {
				dataGenerated.add("SENT_TO_PRINTER_UNREAD" + code + "SKU#1");
			}
			runAllTasks();
		}
	}

}
