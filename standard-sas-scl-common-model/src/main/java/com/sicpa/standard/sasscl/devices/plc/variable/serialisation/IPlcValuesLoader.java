package com.sicpa.standard.sasscl.devices.plc.variable.serialisation;

import java.util.List;

import com.sicpa.standard.plc.value.IPlcVariable;

public interface IPlcValuesLoader {

	/**
	 * load the values into the variables
	 */
	@SuppressWarnings("rawtypes")
	void load(List<IPlcVariable> variables, PlcValuesForAllVar values);

}
