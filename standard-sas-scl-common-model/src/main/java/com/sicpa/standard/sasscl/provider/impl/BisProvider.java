package com.sicpa.standard.sasscl.provider.impl;

import com.sicpa.standard.client.common.provider.AbstractProvider;
import com.sicpa.standard.sasscl.devices.bis.IBisAdaptor;

public class BisProvider extends AbstractProvider<IBisAdaptor> {

	public BisProvider() {
		super("BIS");
	}

}
