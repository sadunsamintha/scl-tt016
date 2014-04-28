package com.sicpa.standard.sasscl.alert;

import com.sicpa.standard.sasscl.ioc.SpringConfig;

public class CameraDuplicatedCodeAlertTestSAS extends CameraDuplicatedCodeAlertTest {

	@Override
	public SpringConfig getSpringConfig() {
		return new SpringConfig();
	}
}
