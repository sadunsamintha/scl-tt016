package com.sicpa.standard.sasscl.devices.printer.impl;

import static com.sicpa.standard.printer.leibinger.driver.leibinger.LeibingerSpecificSettings.CMD_SET_USER_LEVEL;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.printer.controller.PrinterException;
import com.sicpa.standard.printer.leibinger.driver.leibinger.LeibingerUserLevel;
import com.sicpa.standard.sasscl.devices.printer.PrinterAdaptorException;
import com.sicpa.standard.sasscl.devices.printer.xcode.mapping.IMappingExtendedCodeBehavior;
import com.sicpa.standard.sasscl.event.PrinterProfileEvent;

public class PrinterAdaptorLeibinger extends PrinterAdaptor {

	private static final Logger logger = LoggerFactory.getLogger(PrinterAdaptorLeibinger.class);

	private IMappingExtendedCodeBehavior mappingExtendedCodeBehavior;

	@Subscribe
	public void setUserLevel(PrinterProfileEvent event) {
		try {
			controller.sendSpecificSettings(CMD_SET_USER_LEVEL.getValue(), LeibingerUserLevel.get(event.getLevel()));
		} catch (PrinterException e) {
			logger.error("", e.getMessage());
		}
	}

	@Override
	public void sendCodesToPrint(List<String> codes) throws PrinterAdaptorException {
		logger.debug("Printer sending codes to print");
		try {
			controller.sendExtendedCodes(mappingExtendedCodeBehavior.get(getName()).createExCodes(codes));
			codeSent = true;
		} catch (PrinterException e) {
			throw new PrinterAdaptorException("sending codes to printer failed", e);
		}
	}

	public void setMappingExtendedCodeBehavior(IMappingExtendedCodeBehavior mappingExtendedCodeBehavior) {
		this.mappingExtendedCodeBehavior = mappingExtendedCodeBehavior;
	}

}
