package com.sicpa.standard.sasscl.activation.export;

import com.sicpa.standard.sasscl.ioc.SpringConfig;

public class ActivationExportTestSAS extends ActivationExportTest {
		
	@Override
	public SpringConfig getSpringConfig() {
		return new SpringConfig();
	}
}
