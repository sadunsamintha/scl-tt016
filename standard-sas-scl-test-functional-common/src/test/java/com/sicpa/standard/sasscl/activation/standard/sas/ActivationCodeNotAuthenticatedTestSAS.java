package com.sicpa.standard.sasscl.activation.standard.sas;

import com.sicpa.standard.sasscl.activation.standard.ActivationCodeNotAuthenticatedTest;
import com.sicpa.standard.sasscl.model.ProductionMode;

public class ActivationCodeNotAuthenticatedTestSAS extends ActivationCodeNotAuthenticatedTest {

	@Override
	protected ProductionMode getProductionMode() {
		return SAS_MODE;
	}

}
