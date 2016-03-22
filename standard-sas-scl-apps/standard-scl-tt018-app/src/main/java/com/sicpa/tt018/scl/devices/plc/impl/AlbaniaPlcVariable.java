package com.sicpa.tt018.scl.devices.plc.impl;

import java.util.List;

import com.sicpa.standard.sasscl.devices.plc.PlcVariableMap;

public enum AlbaniaPlcVariable  {
	
	PARAM_SENSOR_TYPE;

	public String getVariableName() {
		return PlcVariableMap.get(this.name());
	}

	
	public List<String> getLineVariableNames() {
		return PlcVariableMap.getLineVariableName(getVariableName());
	}
	
}
