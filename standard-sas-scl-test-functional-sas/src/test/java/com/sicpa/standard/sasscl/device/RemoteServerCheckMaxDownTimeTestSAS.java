package com.sicpa.standard.sasscl.device;

import com.sicpa.standard.sasscl.ioc.SpringConfig;

public class RemoteServerCheckMaxDownTimeTestSAS extends RemoteServerCheckMaxDownTime {

	@Override
	public SpringConfig getSpringConfig() {
		return new SpringConfig();
	}
}
