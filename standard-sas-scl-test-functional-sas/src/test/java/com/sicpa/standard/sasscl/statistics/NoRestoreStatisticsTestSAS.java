package com.sicpa.standard.sasscl.statistics;

import com.sicpa.standard.sasscl.ioc.SpringConfig;

public class NoRestoreStatisticsTestSAS extends NoRestoreStatisticsTest {

	@Override
	public SpringConfig getSpringConfig() {
		SpringConfig config = new SpringConfig();
		config.put("reset stats", "spring/statistics-resetAtEachStartBehavior.xml");
		return config;
	}
}
