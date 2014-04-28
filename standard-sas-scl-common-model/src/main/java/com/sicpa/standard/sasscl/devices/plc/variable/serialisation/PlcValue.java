package com.sicpa.standard.sasscl.devices.plc.variable.serialisation;

public class PlcValue {
	protected Object value;
	protected String varName;

	public PlcValue(final Object value, final String varName) {
		this.value = value;
		this.varName = varName;
	}

	public Object getValue() {
		return this.value;
	}

	public void setValue(final Object value) {
		this.value = value;
	}

	public String getVarName() {
		return this.varName;
	}

	public void setVarName(final String varName) {
		this.varName = varName;
	}

    @Override
    public String toString() {

        return "PlcValue{" +
                "value=" + value +
                ", varName='" + varName + '\'' +
                '}';
    }
}
