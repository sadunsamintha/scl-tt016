package com.sicpa.standard.sasscl.activation.standard;

import com.sicpa.standard.sasscl.ioc.SpringConfig;
import com.sicpa.standard.sasscl.ioc.SpringConfigSCL;

public class ActivationNoCodeTypeCheckingTestSCL extends ActivationNoCodeTypeCheckingTest {

	@Override
	public SpringConfig getSpringConfig() {
		return new SpringConfigSCL();
	}
}
