package com.sicpa.standard.sasscl.alert;

import com.sicpa.standard.sasscl.ioc.SpringConfig;
import com.sicpa.standard.sasscl.ioc.SpringConfigSCL;

public class CameraDuplicatedCodeAlertTestSCL extends CameraDuplicatedCodeAlertTest {

	@Override
	public SpringConfig getSpringConfig() {
		return new SpringConfigSCL();
	}
}
