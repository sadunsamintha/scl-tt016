package com.sicpa.standard.sasscl.functionaltests.statistics;

import com.sicpa.standard.sasscl.AbstractFunctionnalTest;
import com.sicpa.standard.sasscl.model.ProductionMode;

public class ResetStatisticsWhenSelectSkuTest extends AbstractFunctionnalTest {

	@Override
	protected ProductionMode getProductionMode() {
		return SCL_MODE;
	}

	public void test() throws Exception {

		init();

		setProductionParameter();

		startProduction();
		checkApplicationStatusRUNNING();

		generateCameraCodes(20, 3);

		stopProduction();
		checkApplicationStatusCONNECTED();

		setProductionParameter();
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
