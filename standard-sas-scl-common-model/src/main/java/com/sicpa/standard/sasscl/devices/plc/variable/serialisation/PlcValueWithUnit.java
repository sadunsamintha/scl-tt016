package com.sicpa.standard.sasscl.devices.plc.variable.serialisation;

import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.unit.PlcUnit;

public class PlcValueWithUnit extends PlcValue {
	protected PlcUnit unit;
	protected String unitVariable;

	public PlcValueWithUnit(final Object value, final String varName, final PlcUnit unit, final String unitVar) {
		super(value, varName);
		this.unit = unit;
		this.unitVariable = unitVar;
	}

	public PlcUnit getUnit() {
		return unit;
	}

	public void setUnit(final PlcUnit unit) {
		this.unit = unit;
	}

	public String getUnitVariable() {
		return unitVariable;
	}

	public void setUnitVariable(final String unitVariable) {
		this.unitVariable = unitVariable;
	}

	@Override
	public String toString() {

		return "PlcValueWithUnit{" + "value=" + value + ", varName=" + varName + ", unit=" + unit + ", unitVariable='"
				+ unitVariable + '\'' + '}';
	}
}
