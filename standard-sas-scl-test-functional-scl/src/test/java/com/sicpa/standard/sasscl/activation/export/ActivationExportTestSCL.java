package com.sicpa.standard.sasscl.activation.export;

import com.sicpa.standard.sasscl.ioc.SpringConfig;
import com.sicpa.standard.sasscl.ioc.SpringConfigSCL;

public class ActivationExportTestSCL extends ActivationExportTest {

	@Override
	public SpringConfig getSpringConfig() {
		return new SpringConfigSCL();
	}
}
