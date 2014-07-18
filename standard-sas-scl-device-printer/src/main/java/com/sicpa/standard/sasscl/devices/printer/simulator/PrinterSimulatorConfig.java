package com.sicpa.standard.sasscl.devices.printer.simulator;

import java.io.Serializable;

public class PrinterSimulatorConfig implements Serializable {

	private static final long serialVersionUID = 0l;

	private int port;

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
}