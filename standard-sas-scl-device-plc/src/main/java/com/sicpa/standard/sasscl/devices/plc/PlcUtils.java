package com.sicpa.standard.sasscl.devices.plc;

import java.util.HashMap;
import java.util.Map;

import com.sicpa.standard.plc.value.IPlcVariable;
import com.sicpa.standard.plc.value.PlcVariable;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.PlcBooleanVariableDescriptor;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.PlcByteVariableDescriptor;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.PlcIntegerVariableDescriptor;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.PlcShortVariableDescriptor;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.PlcVariablePulseParamDescriptor;

public class PlcUtils {

	// tag to create the correct plc var in the plc map , see plc.Vars.groovy
	public static final String d = "distance";
	public static final String i = "int";
	public static final String s = "short";
	public static final String b = "bool";
	public static final String by = "byte";

	public static final Map<String, Map<String, String>> custoInfo = new HashMap<>();

	public static void injectPlcVar(String logicName, Map<String, String> info) {
		custoInfo.put(logicName, info);
	}

	public static IPlcVariable<?> clone(IPlcVariable<?> source, int lineIndex) {
		return PlcVariable.create(
				source.getVariableName().replaceAll(PlcLineHelper.lineIndexPlaceholder, String.valueOf(lineIndex)),
				source.getVariableType());
	}

	public static PlcVariablePulseParamDescriptor createPlcUnitConverterParamDesc(IPlcVariable<Integer> var) {
		PlcVariablePulseParamDescriptor desc = new PlcVariablePulseParamDescriptor();
		desc.setVariable(var);
		desc.setMin(0);
		desc.setMax(999999);
		return desc;
	}

	public static PlcIntegerVariableDescriptor createPlcIntegerDesc(IPlcVariable<Integer> var) {
		PlcIntegerVariableDescriptor desc = new PlcIntegerVariableDescriptor();
		desc.setVariable(var);
		desc.setMin(0);
		desc.setMax(999999);
		return desc;
	}

	public static PlcBooleanVariableDescriptor createPlcBooleanDesc(IPlcVariable<Boolean> var) {
		PlcBooleanVariableDescriptor desc = new PlcBooleanVariableDescriptor();
		desc.setVariable(var);
		return desc;
	}

	public static PlcShortVariableDescriptor createPlcShortDesc(IPlcVariable<Short> var) {
		PlcShortVariableDescriptor desc = new PlcShortVariableDescriptor();
		desc.setVariable(var);
		desc.setMin((short) 0);
		desc.setMax((short) 9999);
		return desc;
	}

	public static PlcByteVariableDescriptor createPlcByteDesc(IPlcVariable<Byte> var) {
		PlcByteVariableDescriptor desc = new PlcByteVariableDescriptor();
		desc.setVariable(var);
		desc.setMin((byte) 0);
		desc.setMax((byte) 255);
		return desc;
	}

}
