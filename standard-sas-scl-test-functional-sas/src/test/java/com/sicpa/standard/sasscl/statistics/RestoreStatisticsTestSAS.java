package com.sicpa.standard.sasscl.statistics;

import com.sicpa.standard.client.common.ioc.PropertyPlaceholderResources;
import com.sicpa.standard.sasscl.devices.remote.simulator.RemoteServerSimulator;
import com.sicpa.standard.sasscl.ioc.SpringConfig;

public class RestoreStatisticsTestSAS extends RestoreStatisticsTest {

	@Override
	public SpringConfig getSpringConfig() {
		return new SpringConfig();
	}

	@Override
	public void init() {
		// needed because we need to generate an encoder
		PropertyPlaceholderResources.addProperties("remoteServerSimulator", RemoteServerSimulator.class.getName());

		super.init();
	}
}
