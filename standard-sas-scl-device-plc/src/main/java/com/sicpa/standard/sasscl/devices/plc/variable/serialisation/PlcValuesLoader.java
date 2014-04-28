package com.sicpa.standard.sasscl.devices.plc.variable.serialisation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sicpa.standard.plc.value.IPlcVariable;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.unit.PlcUnit;

public class PlcValuesLoader implements IPlcValuesLoader {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void load(List<IPlcVariable> variables, PlcValuesForAllVar values) {

		Map<String, Object> mapValues = new HashMap<String, Object>();

		for (PlcValue value : values.getListValues()) {
			mapValues.put(value.getVarName(), value.getValue());
			if (value instanceof PlcValueWithUnit) {
				String unitVar = ((PlcValueWithUnit) value).getUnitVariable();
				mapValues.put(unitVar, ((PlcValueWithUnit) value).getUnit() == PlcUnit.PULSE);
			}
		}

		for (IPlcVariable var : variables) {
			Object val = mapValues.get(var.getVariableName());
			if (val != null) {
				var.setValue(val);
			}
		}

	};

}
