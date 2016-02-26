package com.sicpa.standard.sasscl.storage;

import com.sicpa.standard.sasscl.ioc.SpringConfig;
import com.sicpa.standard.sasscl.ioc.SpringConfigSCL;

public class RemoveAllProductionDataTestSCL extends RemoveAllProductionDataTest {

	@Override
	public SpringConfig getSpringConfig() {
		return new SpringConfigSCL();
	}
}
