package com.sicpa.standard.sasscl.devices.plc.variable.descriptor.event;

public class PulseConversionParamChangedEvent {

	private String paramName;
	private int value;

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

	@Override
	public String toString() {
		return "PulseConversionParamChangedEvent [paramName=" + paramName + ", value=" + value + "]";
	}
}
