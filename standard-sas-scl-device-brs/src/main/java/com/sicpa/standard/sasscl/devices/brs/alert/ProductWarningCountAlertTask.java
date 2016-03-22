package com.sicpa.standard.sasscl.devices.brs.alert;

import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.messages.MessageEventKey;

public class ProductWarningCountAlertTask extends AbstractBrsProductCountAlertTask {

	private static final String ALERT_NAME = "Brs Product Warning Count Alert";

	@Override
	public MessageEvent getAlertMessage() {
		return new MessageEvent(MessageEventKey.BRS.BRS_TOO_MANY_UNREAD_BARCODES_WARNING);
	}

	@Override
	public String getAlertName() {
		return ALERT_NAME;
	}
}
