package com.sicpa.standard.sasscl.devices.remote.impl;

import com.sicpa.standard.sasscl.config.xstream.IXStreamConfigurator;
import com.thoughtworks.xstream.XStream;

public class RemoteServerXStreamConfigurator implements IXStreamConfigurator {

	@Override
	public void configure(XStream xstream) {
		xstream.alias("RemoteServerModel", RemoteServerModel.class);
	}
}
