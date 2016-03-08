package com.sicpa.standard.sasscl.devices.plc;

import java.util.HashMap;
import java.util.Map;

import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.PlcBooleanVariableDescriptor;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.PlcByteVariableDescriptor;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.PlcIntegerVariableDescriptor;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.PlcShortVariableDescriptor;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.PlcVariablePulseParamDescriptor;

public class PlcUtils {

	// tag to create the correct plc var in the plc map , see plc.Vars.groovy
	public static final String D = "distance";
	public static final String I = "int";
	public static final String S = "short";
	public static final String B = "bool";
	public static final String BY = "byte";

	public static final String LINE = ".com.stLine[#x].";
	public static final String CAB = ".com.stCabinet.";
	public static final String LINE_PRM = LINE + "stParameters.";
	public static final String CAB_PRM = CAB + "stParameters.";
	public static final String LINE_NTF = LINE + "stNotifications.";
	public static final String CAB_NTF = CAB + "stNotifications.";

	public static final Map<String, Map<String, String>> custoInfo = new HashMap<>();

	public static void injectPlcVar(String logicName, Map<String, String> info) {
		custoInfo.put(logicName, info);
	}

	public static PlcVariablePulseParamDescriptor createPlcUnitConverterParamDesc(String varName) {
		PlcVariablePulseParamDescriptor desc = new PlcVariablePulseParamDescriptor();
		desc.setVarName(varName);
		return desc;
	}

	public static PlcIntegerVariableDescriptor createPlcIntegerDesc(String varName) {
		PlcIntegerVariableDescriptor desc = new PlcIntegerVariableDescriptor();
		desc.setVarName(varName);
		return desc;
	}

	public static PlcBooleanVariableDescriptor createPlcBooleanDesc(String varName) {
		PlcBooleanVariableDescriptor desc = new PlcBooleanVariableDescriptor();
		desc.setVarName(varName);
		return desc;
	}

	public static PlcShortVariableDescriptor createPlcShortDesc(String varName) {
		PlcShortVariableDescriptor desc = new PlcShortVariableDescriptor();
		desc.setVarName(varName);
		return desc;
	}

	public static PlcByteVariableDescriptor createPlcByteDesc(String varName) {
		PlcByteVariableDescriptor desc = new PlcByteVariableDescriptor();
		desc.setVarName(varName);
		return desc;
	}

}
