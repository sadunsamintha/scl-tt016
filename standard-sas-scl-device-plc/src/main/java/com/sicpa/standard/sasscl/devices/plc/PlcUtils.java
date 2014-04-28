package com.sicpa.standard.sasscl.devices.plc;

import com.sicpa.standard.plc.value.IPlcVariable;
import com.sicpa.standard.plc.value.PlcVariable;

public class PlcUtils {

	public static IPlcVariable<?> clone(IPlcVariable<?> source, int lineIndex) {
		return PlcVariable.create(
				source.getVariableName().replaceAll(PlcVariableMap.lineIndexPlaceholder, String.valueOf(lineIndex)),
				source.getVariableType());
	}

}
