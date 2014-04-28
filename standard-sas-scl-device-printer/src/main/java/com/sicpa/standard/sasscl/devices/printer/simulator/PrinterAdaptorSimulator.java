package com.sicpa.standard.sasscl.devices.printer.simulator;

import com.sicpa.standard.sasscl.devices.printer.impl.PrinterAdaptor;

public class PrinterAdaptorSimulator extends PrinterAdaptor implements IPrinterAdaptorSimulator {

	public PrinterAdaptorSimulator() {
	}

//	public PrinterAdaptorSimulator(final IPrinterController controller) {
//		super();
//		setController(controller);
//	}

	@Override
	public IPrinterControllerSimulator getSimulatorController() {
		return (IPrinterControllerSimulator) this.controller;
	}

}
