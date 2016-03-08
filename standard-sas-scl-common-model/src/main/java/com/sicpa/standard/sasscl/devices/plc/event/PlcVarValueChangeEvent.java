package com.sicpa.standard.sasscl.devices.plc.event;

public class PlcVarValueChangeEvent {

	private final String logicVarName;
	private final String value;
	private final int lineIndex;

	public PlcVarValueChangeEvent(String logicVarName, String value, int lineIndex) {
		super();
		this.logicVarName = logicVarName;
		this.value = value;
		this.lineIndex = lineIndex;
	}

	public String getLogicVarName() {
		return logicVarName;
	}

	public String getValue() {
		return value;
	}

	public int getLineIndex() {
		return lineIndex;
	}

}
