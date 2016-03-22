package com.sicpa.standard.sasscl.devices.plc.variable.descriptor.event;

public class PulseConversionParamChangedEvent {

	private String paramName;
	private int value;
	private int line;

	public PulseConversionParamChangedEvent(String paramName, int value, int line) {
		this.paramName = paramName;
		this.value = value;
		this.line = line;
	}

	public int getLine() {
		return line;
	}

	public String getParamName() {
		return paramName;
	}

	public int getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "PulseConversionParamChangedEvent [paramName=" + paramName + ", value=" + value + ", line=" + line + "]";
	}

}
