package com.sicpa.standard.sasscl.devices.plc.variable;

import java.util.List;

public class PlcVariableGroupEvent {

	private final List<PlcVariableGroup> groups;
	private final String lineId;

	public PlcVariableGroupEvent(List<PlcVariableGroup> groups, String lineId) {
		this.groups = groups;
		this.lineId = lineId;
	}

	public List<PlcVariableGroup> getGroups() {
		return groups;
	}

	public String getLineId() {
		return lineId;
	}
}
