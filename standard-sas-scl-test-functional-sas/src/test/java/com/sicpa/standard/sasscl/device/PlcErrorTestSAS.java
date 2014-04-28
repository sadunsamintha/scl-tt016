package com.sicpa.standard.sasscl.device;

import com.sicpa.standard.sasscl.ioc.SpringConfig;

public class PlcErrorTestSAS extends PlcErrorTest {

	@Override
	public SpringConfig getSpringConfig() {
		return new SpringConfig();
	}
}
