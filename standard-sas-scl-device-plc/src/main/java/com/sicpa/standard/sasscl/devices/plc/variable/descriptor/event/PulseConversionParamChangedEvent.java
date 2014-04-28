package com.sicpa.standard.sasscl.devices.plc.variable.descriptor.event;

public class PulseConversionParamChangedEvent {

	protected String paramName;
	protected int value;

	public PulseConversionParamChangedEvent(String paramName, int value) {
		this.paramName = paramName;
		this.value = value;
	}

	public String getParamName() {
		return paramName;
	}

	public int getValue() {
		return value;
	}
}
