package com.sicpa.standard.sasscl.devices.plc.variable;

public class PlcVariableGroupEvent {

	protected EditablePlcVariables group;
	protected String lineId;

	public PlcVariableGroupEvent(EditablePlcVariables group, String lineId) {
		this.group = group;
		this.lineId = lineId;
	}

	public EditablePlcVariables getGroup() {
		return group;
	}

	public String getLineId() {
		return lineId;
	}
}
