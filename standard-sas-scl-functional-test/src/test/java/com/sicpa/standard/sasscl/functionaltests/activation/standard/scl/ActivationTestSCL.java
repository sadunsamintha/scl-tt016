package com.sicpa.standard.sasscl.functionaltests.activation.standard.scl;

import com.sicpa.standard.sasscl.activation.standard.ActivationTest;
import com.sicpa.standard.sasscl.model.ProductionMode;

public class ActivationTestSCL extends ActivationTest {

	@Override
	protected ProductionMode getProductionMode() {
		return SCL_MODE;
	}

}
