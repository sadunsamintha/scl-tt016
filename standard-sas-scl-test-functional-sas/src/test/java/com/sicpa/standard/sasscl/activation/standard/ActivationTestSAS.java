package com.sicpa.standard.sasscl.activation.standard;

import com.sicpa.standard.sasscl.ioc.SpringConfig;

public class ActivationTestSAS extends ActivationTest {

	@Override
	public SpringConfig getSpringConfig() {
		return new SpringConfig();
	}
}
