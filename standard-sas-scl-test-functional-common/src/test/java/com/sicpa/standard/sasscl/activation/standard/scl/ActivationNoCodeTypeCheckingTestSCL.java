package com.sicpa.standard.sasscl.activation.standard.scl;

import com.sicpa.standard.sasscl.activation.standard.ActivationNoCodeTypeCheckingTest;
import com.sicpa.standard.sasscl.model.ProductionMode;

public class ActivationNoCodeTypeCheckingTestSCL extends ActivationNoCodeTypeCheckingTest {

	@Override
	protected ProductionMode getProductionMode() {
		return SCL_MODE;
	}
}
