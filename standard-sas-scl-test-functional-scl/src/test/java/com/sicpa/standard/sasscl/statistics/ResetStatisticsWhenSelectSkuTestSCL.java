package com.sicpa.standard.sasscl.statistics;

import com.sicpa.standard.sasscl.ioc.SpringConfig;
import com.sicpa.standard.sasscl.ioc.SpringConfigSCL;

public class ResetStatisticsWhenSelectSkuTestSCL extends ResetStatisticsWhenSelectSkuTest {

	@Override
	public SpringConfig getSpringConfig() {
		return new SpringConfigSCL();
	}
}
