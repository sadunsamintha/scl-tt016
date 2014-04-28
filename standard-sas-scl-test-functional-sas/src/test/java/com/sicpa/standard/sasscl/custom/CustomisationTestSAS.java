package com.sicpa.standard.sasscl.custom;

import com.sicpa.standard.sasscl.ioc.SpringConfig;

public class CustomisationTestSAS extends CustomisationTest {

	@Override
	public SpringConfig getSpringConfig() {
		SpringConfig config = new SpringConfig();
		config.put(SpringConfig.OFFLINE_COUNTING, "spring/offlineCounting.xml");
		return config;
	}
}
