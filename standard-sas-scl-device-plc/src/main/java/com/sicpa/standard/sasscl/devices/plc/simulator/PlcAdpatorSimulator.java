package com.sicpa.standard.sasscl.devices.plc.simulator;

import com.sicpa.standard.plc.controller.IPlcController;
import com.sicpa.standard.sasscl.devices.plc.impl.PlcAdaptor;

public class PlcAdpatorSimulator extends PlcAdaptor {

	public PlcAdpatorSimulator() {
	}

	public PlcAdpatorSimulator(final IPlcController<?> controller) {
		super(controller);
	}

}
