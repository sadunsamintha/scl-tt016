package com.sicpa.standard.sasscl.provider.impl;

import com.sicpa.standard.client.common.provider.AbstractProvider;
import com.sicpa.standard.sasscl.devices.plc.IPlcAdaptor;

public class PlcProvider extends AbstractProvider<IPlcAdaptor> {
	
	public PlcProvider() {
		super("PLC");
	}
}
