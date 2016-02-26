package com.sicpa.standard.sasscl.device;

import com.sicpa.standard.sasscl.ioc.SpringConfig;
import com.sicpa.standard.sasscl.ioc.SpringConfigSCL;

public class PlcErrorTestSCL extends PlcErrorTest {

	@Override
	public SpringConfig getSpringConfig() {
		return new SpringConfigSCL();
	}
}
