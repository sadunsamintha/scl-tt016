package com.sicpa.standard.sasscl.statistics;

import com.sicpa.standard.sasscl.AbstractFunctionnalTest;
import com.sicpa.standard.sasscl.model.ProductionMode;

public abstract class ResetStatisticsWhenSelectSkuTest extends AbstractFunctionnalTest {

	public void test() throws Exception {

		init();

		setProductionParameter(1, 1, ProductionMode.STANDARD);

		startProduction();
		checkApplicationStatusRUNNING();

		generateCameraCodes(20, 3);

		stopProduction();
		checkApplicationStatusCONNECTED();

		setProductionParameter(2, 1, ProductionMode.STANDARD);
		checkStatistics(0, 0);
		exit();
	}

	public void generateCameraCodes(int good, int bad) throws Exception {
		for (int i = 0; i < good; i++) {
			String code = generateACodeFromEncoder();
			camera.fireGoodCode(code);
		}

		for (int i = 0; i < bad; i++) {
			String code = "B00" + i;
			camera.fireBadCode(code);
		}
	}
}
