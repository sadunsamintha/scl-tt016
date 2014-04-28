package com.sicpa.standard.sasscl.alert;

import com.sicpa.standard.sasscl.ioc.SpringConfig;

public class CameraBadCodeAlertTestSAS extends CameraBadCodeAlertTest {

	@Override
	public SpringConfig getSpringConfig() {
		return new SpringConfig();
	}
}
