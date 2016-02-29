package com.sicpa.standard.sasscl.activation.standard.sas;

import com.sicpa.standard.sasscl.activation.standard.ActivationNoCodeTypeCheckingTest;
import com.sicpa.standard.sasscl.model.ProductionMode;

public class ActivationNoCodeTypeCheckingTestSAS extends ActivationNoCodeTypeCheckingTest {

	@Override
	protected ProductionMode getProductionMode() {
		return SAS_MODE;
	}
}
