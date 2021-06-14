/**
 * Author	: YYang
 * Date		: Oct 7, 2010
 *
 * Copyright (c) 2010 SICPA Security Solutions, all rights reserved.
 *
 */
package com.sicpa.standard.sasscl.devices.plc.event;

import lombok.ToString;

/**
 * Encapsulate PLC event
 * 
 */
@ToString
public class PlcEvent {

	/**
	 * variable name of PLC
	 */
	protected final String varName;

	/**
	 * value of the PLC variable
	 */
	protected final Object value;

	public PlcEvent(String varName, Object value) {
		this.varName = varName;
		this.value = value;
	}

	public String getVarName() {
		return this.varName;
	}

	public Object getValue() {
		return this.value;
	}

	@Override
	public boolean equals(Object o) {

		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		PlcEvent plcEvent = (PlcEvent) o;

		if (value != null ? !value.equals(plcEvent.value) : plcEvent.value != null)
			return false;
		if (varName != null ? !varName.equals(plcEvent.varName) : plcEvent.varName != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {

		int result = varName != null ? varName.hashCode() : 0;
		result = 31 * result + (value != null ? value.hashCode() : 0);
		return result;
	}
}
