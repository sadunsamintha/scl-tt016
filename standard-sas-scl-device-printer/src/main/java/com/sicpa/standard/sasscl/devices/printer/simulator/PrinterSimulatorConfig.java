package com.sicpa.standard.sasscl.devices.printer.simulator;

import java.io.Serializable;

import com.sicpa.standard.printer.controller.model.IPrinterModel;

public class PrinterSimulatorConfig implements IPrinterModel, Serializable {

	private static final long serialVersionUID = 0l;

	private int port;

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public int getTcpTimeout() {
		
		return 1000;
	}

}