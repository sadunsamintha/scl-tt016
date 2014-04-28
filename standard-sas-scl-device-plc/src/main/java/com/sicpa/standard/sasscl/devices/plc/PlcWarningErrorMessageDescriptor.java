package com.sicpa.standard.sasscl.devices.plc;

public class PlcWarningErrorMessageDescriptor {

	protected String messageKey;

	public PlcWarningErrorMessageDescriptor() {
	}

	public PlcWarningErrorMessageDescriptor(String messageKey) {
		this.messageKey = messageKey;
	}

	public String getMessageKey() {
		return messageKey;
	}

	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}

}
