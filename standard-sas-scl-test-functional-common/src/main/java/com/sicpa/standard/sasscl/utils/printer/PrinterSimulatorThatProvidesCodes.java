package com.sicpa.standard.sasscl.utils.printer;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.sicpa.standard.sasscl.devices.camera.simulator.ICodeProvider;
import com.sicpa.standard.sasscl.devices.printer.simulator.PrinterSimulatorConfig;

public class PrinterSimulatorThatProvidesCodes extends PrinterSimulatorController implements ICodeProvider {

	protected final BlockingQueue<String> codes = new LinkedBlockingQueue<String>();

	public PrinterSimulatorThatProvidesCodes() {
		super();
	}

	public PrinterSimulatorThatProvidesCodes(final PrinterSimulatorConfig config) {
		super(config);
	}

	public PrinterSimulatorThatProvidesCodes(final String config) {
		super(config);
	}

	@Override
	public String requestCode() {

		if (isStarted()) {

			String code = null;

			try {
				code = this.codes.poll(1, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
			}

			while (code == null) {
				askCodes();
				try {
					code = this.codes.poll(1, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
				}
			}

			return code;

		} else {
			return null;
		}

	}

	@Override
	public void sendCodes(final List<String> codes) {
		super.sendCodes(codes);
		this.codes.addAll(codes);
	}
}