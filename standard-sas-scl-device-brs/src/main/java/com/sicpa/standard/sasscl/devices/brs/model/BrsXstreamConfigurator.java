package com.sicpa.standard.sasscl.devices.brs.model;

import com.sicpa.standard.client.common.xstream.IXStreamConfigurator;
import com.sicpa.standard.sasscl.devices.brs.simulator.BrsSimulatorConfig;
import com.thoughtworks.xstream.XStream;

public class BrsXstreamConfigurator implements IXStreamConfigurator {

	@Override
	public void configure(XStream x) {
		x.alias("BrsModel", BrsModel.class);
		x.alias("BrsSimulatorConfig", BrsSimulatorConfig.class);
	}

}
