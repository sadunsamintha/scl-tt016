package com.sicpa.standard.sasscl.statistics;

import com.sicpa.standard.sasscl.ioc.SpringConfig;
import com.sicpa.standard.sasscl.ioc.SpringConfigSCL;

public class NoRestoreStatisticsTestSCL extends NoRestoreStatisticsTest {

	@Override
	public SpringConfig getSpringConfig() {
		SpringConfigSCL config = new SpringConfigSCL();
		config.put("reset stats", "spring/statistics-resetAtEachStartBehavior.xml");
		return config;
	}
}
