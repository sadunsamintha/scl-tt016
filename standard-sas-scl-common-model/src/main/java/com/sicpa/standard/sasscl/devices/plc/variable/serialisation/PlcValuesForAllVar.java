package com.sicpa.standard.sasscl.devices.plc.variable.serialisation;

import java.util.ArrayList;
import java.util.List;

public class PlcValuesForAllVar {

	protected List<PlcValue> listValues;

	public PlcValuesForAllVar() {
		this.listValues = new ArrayList<PlcValue>();
	}

	public Object getValue(final String varName) {
		for (PlcValue v : this.listValues) {
			if (v.getVarName().equals(varName)) {
				return v.getValue();
			}
		}
		return null;
	}

	public PlcValue get(final String varName) {
		for (PlcValue v : this.listValues) {
			if (v.getVarName().equals(varName)) {
				return v;
			}
		}
		return null;
	}

	public void add(final PlcValue value) {
		this.listValues.add(value);
	}
	
	public List<PlcValue> getListValues() {
		return listValues;
	}
}
