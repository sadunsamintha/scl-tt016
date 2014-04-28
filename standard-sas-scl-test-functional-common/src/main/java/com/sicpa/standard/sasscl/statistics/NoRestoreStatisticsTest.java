package com.sicpa.standard.sasscl.statistics;

import com.sicpa.standard.sasscl.AbstractFunctionnalTest;
import com.sicpa.standard.sasscl.model.ProductionMode;

public abstract class NoRestoreStatisticsTest extends AbstractFunctionnalTest {

	public void test() {

		init();

		setProductionParameter(1, 1, ProductionMode.STANDARD);

		startProduction();
		runAllTasks();
		checkApplicationStatusRUNNING();

		generateCameraCodes(20, 3);
		stopProduction();
		runAllTasks();
		checkApplicationStatusCONNECTED();

		startProduction();
		checkApplicationStatusRUNNING();

		stopProduction();
		checkApplicationStatusCONNECTED();
		checkStatistics(0, 0);

		exit();
	}

	public void generateCameraCodes(int good, int bad) {
		for (int i = 0; i < good; i++) {
			String code = "100" + i;
			camera.fireGoodCode(code);
		}

		for (int i = 0; i < bad; i++) {
			String code = "B00" + i;
			camera.fireBadCode(code);
		}
	}
}
