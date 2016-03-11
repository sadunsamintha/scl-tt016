package com.sicpa.standard.sasscl.functionaltests.activation.standard.sas;

import com.sicpa.standard.sasscl.activation.standard.ActivationCodeTypeMismatchTest;
import com.sicpa.standard.sasscl.model.ProductionMode;

public class ActivationCodeTypeMismatchTestSAS extends ActivationCodeTypeMismatchTest {

	@Override
	protected ProductionMode getProductionMode() {
		return SAS_MODE;
	}
}
