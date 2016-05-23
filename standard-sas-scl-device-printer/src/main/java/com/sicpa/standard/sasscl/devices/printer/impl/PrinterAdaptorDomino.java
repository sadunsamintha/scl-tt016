package com.sicpa.standard.sasscl.devices.printer.impl;

import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.printer.controller.IPrinterController;
import com.sicpa.standard.printer.controller.PrinterException;
import com.sicpa.standard.printer.controller.model.command.PrinterMessage;
import com.sicpa.standard.printer.controller.model.command.PrinterMessageId;
import com.sicpa.standard.sasscl.devices.printer.PrinterAdaptorException;

public class PrinterAdaptorDomino extends PrinterAdaptor {

	private static final Logger logger = LoggerFactory.getLogger(PrinterAdaptorDomino.class);
	private static final String PRINTER_MESSAGE_PREFIX = "PRINTER.";

	public PrinterAdaptorDomino() {
		super();
	}

	public PrinterAdaptorDomino(IPrinterController controller) {
		super(controller);
	}

	private Collection<String> acceptedAlerts = asList(
			//@formatter:off
			"VENTILATION_STOPPED",
			"C_CHRG_DET_FAILED",
			"C_WET_CHRG_ELECTRODE",
			"HVC_HV_TRIPPED",
			"VISCOSITY_OUT_OF_RANGE",
			"VISCOMETER_FAULT",
			"W_INK_LOW",
			"F_SUMP_EMPTY",
			"W_MC_MODN_FALL_BACK",
			"F_GUTTER_DRY",
			"W_MAKEUP_LOW", 
			"F_INK_HIGH",
			"F_MAKEUP_HIGH",
			"F_MAKEUP_EMPTY",
			"S_WAKEUP_ON",
			"W_BLEEDING_DAMPER",
			"W_PSB_BATTERY_LOW",
			"W_MAINTENANCE_DUE",
			"W_MAINTENANCE_2HOURS",
			"F_MAINTENANCE_NOW");
	//@formatter:on

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

	@Override
	protected void onMessageReceived(PrinterMessage msg) {
		if (isAlarmList(msg)) {
			handleAlarmList(msg);
		} else {
			super.onMessageReceived(msg);
		}
	}

	private boolean isAlarmList(PrinterMessage msg) {
		return msg.getKey().equals(PrinterMessageId.ALARM_LIST);
	}

	private void handleAlarmList(PrinterMessage msg) {
		// new message communication channel for domino "munch" version
		String desc = msg.getDescription();
		if (isNoAlarm(msg)) {
			String msgKey = getAlarmId(desc);
			String appKey = PRINTER_MESSAGE_PREFIX + msgKey;
			if (acceptAlert(msgKey)) {
				if (msg.isIssueSolved()) {
					logger.info("alarm solved:" + desc);
					fireIssueSolved(appKey);
				} else {
					logger.info("alarm :" + desc);
					fireMessage(appKey, "");
				}
			} else {
				logger.info("alarm ignored:" + desc);
			}
		}
	}

	private boolean isNoAlarm(PrinterMessage msg) {
		String desc = msg.getDescription();
		return desc == null;
	}

	private boolean acceptAlert(String msgKey) {
		return acceptedAlerts.contains(msgKey);
	}

	private String getAlarmId(String msgDesc) {
		// format: id/key
		return msgDesc.substring(msgDesc.indexOf('/') + 1);
	}
}
