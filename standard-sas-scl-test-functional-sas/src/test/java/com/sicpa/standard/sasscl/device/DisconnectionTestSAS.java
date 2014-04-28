package com.sicpa.standard.sasscl.device;

import com.sicpa.standard.sasscl.ioc.SpringConfig;

public class DisconnectionTestSAS extends DisconnectionTest {

	@Override
	public SpringConfig getSpringConfig() {
		return new SpringConfig();
	}
}
