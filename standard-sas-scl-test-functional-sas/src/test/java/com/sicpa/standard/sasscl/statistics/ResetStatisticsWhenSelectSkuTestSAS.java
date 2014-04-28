package com.sicpa.standard.sasscl.statistics;

import com.sicpa.standard.sasscl.ioc.SpringConfig;

public class ResetStatisticsWhenSelectSkuTestSAS extends ResetStatisticsWhenSelectSkuTest {

	@Override
	public SpringConfig getSpringConfig() {
		return new SpringConfig();
	}
}
