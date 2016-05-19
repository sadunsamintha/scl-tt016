package com.sicpa.standard.sasscl.devices.printer.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.printer.controller.PrinterException;
import com.sicpa.standard.sasscl.devices.printer.PrinterAdaptorException;

public class PrinterAdaptorDomino extends PrinterAdaptor {

	private static final Logger logger = LoggerFactory.getLogger(PrinterAdaptorDomino.class);

	@Override
	public void sendCodesToPrint(List<String> codes) throws PrinterAdaptorException {
		logger.debug("Printer sending codes to print");
		try {
			controller.sendCodes(codes);
			codeSent = true;
		} catch (PrinterException e) {
			throw new PrinterAdaptorException("sending codes to printer failed", e);
		}
	}
}
