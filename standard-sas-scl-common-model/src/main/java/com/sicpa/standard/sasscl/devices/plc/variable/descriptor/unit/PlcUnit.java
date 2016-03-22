package com.sicpa.standard.sasscl.devices.plc.variable.descriptor.unit;

public enum PlcUnit {

	MS("ms"), MM("mm");

	private String suffix;

	PlcUnit(String suffix) {
		this.suffix = suffix;
	}

	public String getSuffix() {
		return suffix;
	}

	@Override
	public String toString() {
		return getSuffix();
	}

	public static PlcUnit extractUnitFromValue(String value) {
		if (value.endsWith(MM.getSuffix())) {
			return MM;
		} else if (value.endsWith(MS.getSuffix())) {
			return MS;
		} else {
			throw new IllegalArgumentException("unit is missing for " + value);
		}
	}

}
