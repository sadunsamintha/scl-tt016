package com.sicpa.standard.sasscl.storage;

import com.sicpa.standard.sasscl.ioc.SpringConfig;

public class RemoveAllProductionDataTestSAS extends RemoveAllProductionDataTest {

	@Override
	public SpringConfig getSpringConfig() {
		return new SpringConfig();
	}

}
