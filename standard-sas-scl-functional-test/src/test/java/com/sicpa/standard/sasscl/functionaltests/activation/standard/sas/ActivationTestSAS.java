package com.sicpa.standard.sasscl.functionaltests.activation.standard.sas;

import com.sicpa.standard.sasscl.activation.standard.ActivationTest;
import com.sicpa.standard.sasscl.model.ProductionMode;

public class ActivationTestSAS extends ActivationTest {

	@Override
	protected ProductionMode getProductionMode() {
		return SAS_MODE;
	}

}
