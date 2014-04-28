package com.sicpa.standard.sasscl.activation.standard;

import com.sicpa.standard.client.common.ioc.PropertyPlaceholderResources;
import com.sicpa.standard.sasscl.devices.remote.simulator.RemoteServerSimulator;
import com.sicpa.standard.sasscl.ioc.SpringConfig;

public class ActivationCodeTypeMismatchTestSAS extends ActivationCodeTypeMismatchTest {

	@Override
	public SpringConfig getSpringConfig() {
		return new SpringConfig();
	}

	@Override
	public void init() {

		// needed because we need to generate an encoder to get a wrong codetype
		// code
		PropertyPlaceholderResources.addProperties("remoteServerSimulator", RemoteServerSimulator.class.getName());

		super.init();
	}
}
