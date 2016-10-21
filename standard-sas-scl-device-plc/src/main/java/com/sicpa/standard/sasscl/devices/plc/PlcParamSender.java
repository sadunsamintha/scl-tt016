package com.sicpa.standard.sasscl.devices.plc;

import com.sicpa.standard.client.common.device.plc.IPLCVariableMappping;
import com.sicpa.standard.plc.value.IPlcVariable;
import com.sicpa.standard.plc.value.PlcVariable;
import com.sicpa.standard.sasscl.devices.plc.PlcUtils.PLC_TYPE;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.converter.PlcPulseToMMConverterHandler;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.unit.PlcUnit;
import com.sicpa.standard.sasscl.provider.impl.PlcProvider;

import java.util.HashMap;
import java.util.Map;

public class PlcParamSender implements IPlcParamSender {

	private static final String UNIT_SUFFIX = "_TYPE";

	private PlcProvider plcProvider;
	private IPLCVariableMappping plcVarMapping;
	private PlcPulseToMMConverterHandler converterMMtoPulse;
	private final Map<String, PLC_TYPE> typeByPlcVar = new HashMap<>();

	@Override
	public void sendToPlc(String plcLogicalVarName, String value, int lineIndex) throws PlcAdaptorException {
		final boolean reloadParams = false;
		sendToPlc(plcLogicalVarName, value, lineIndex, reloadParams);
	}

	@Override
	public void sendToPlcAndReloadParams(String plcLogicalVarName, String value, int lineIndex) throws PlcAdaptorException {
		final boolean reloadParams = true;
		sendToPlc(plcLogicalVarName, value, lineIndex, reloadParams);
	}

	private void sendToPlc(String plcLogicalVarName, String value, int lineIndex, boolean reloadParams) throws PlcAdaptorException {
		String valueToSend;
		if (conversionNeeded(value)) {
			valueToSend = convertValue(plcLogicalVarName, value, lineIndex);
			sendUnitVar(plcLogicalVarName, value, lineIndex);
		} else {
			valueToSend = value;
		}
		findVarTypeAndSendToPlc(plcLogicalVarName, valueToSend, lineIndex);
		if(reloadParams) {
			plcProvider.get().executeRequest(PlcRequest.RELOAD_PLC_PARAM);
		}
	}

	private IPlcVariable<?> createVar(String logicalName, String value, int lineIndex) {
		String physicalName = getPhysicalLineVariable(logicalName, lineIndex);
		PLC_TYPE type = typeByPlcVar.get(logicalName);
		switch (type) {
		case BY:
			return createByteVar(physicalName, value);
		case S:
			return createShortVar(physicalName, value);
		case I:
		case D:
			return createIntVar(physicalName, value);
		case B:
			return createBooleanVar(physicalName, value);
		default:
			throw new IllegalArgumentException("no type found for:" + logicalName);
		}
	}

	private String getPhysicalLineVariable(String logicalName, int lineIndex) {
		String physicalName = plcVarMapping.getPhysicalVariableName(logicalName);
		physicalName = PlcLineHelper.replaceLinePlaceholder(physicalName, lineIndex);
		return physicalName;
	}

	private IPlcVariable<Byte> createByteVar(String name, String value) {
		byte bval = Byte.parseByte(value);
		IPlcVariable<Byte> var = PlcVariable.createByteVar(name);
		var.setValue(bval);
		return var;
	}

	private IPlcVariable<Short> createShortVar(String name, String value) {
		short sval = Short.parseShort(value);
		IPlcVariable<Short> var = PlcVariable.createShortVar(name, sval);
		return var;
	}

	private IPlcVariable<Integer> createIntVar(String name, String value) {
		//parse float as it can be a float in the config file, because of switch between MM and MS
		int ival = (int) Float.parseFloat(value);
		IPlcVariable<Integer> var = PlcVariable.createInt32Var(name);
		var.setValue(ival);
		return var;
	}

	private IPlcVariable<Boolean> createBooleanVar(String name, String value) {
		boolean bval = Boolean.parseBoolean(value);
		return PlcVariable.createBooleanVar(name, bval);
	}

	private void sendUnitVar(String parentVarName, String value, int lineIndex) throws PlcAdaptorException {
		String unitLogicalVarName = parentVarName + UNIT_SUFFIX;
		String physicalName = getPhysicalLineVariable(unitLogicalVarName, lineIndex);
		IPlcVariable<Boolean> var = createBooleanVar(physicalName, createUnitValue(value) + "");
		plcProvider.get().write(var);
	}

	private String convertValue(String varName, String value, int lineIndex) {
		if (value.endsWith(PlcUnit.MM.getSuffix())) {
			return convertToPulses(value, lineIndex) + "";
		} else if (value.endsWith(PlcUnit.MS.getSuffix())) {
			return value.replace(PlcUnit.MS.getSuffix(), "").trim();
		} else if (value.endsWith(PlcUnit.PULSES.getSuffix())) {
			return value.replace(PlcUnit.PULSES.getSuffix(), "").trim();
		} else {
			throw new IllegalArgumentException("unit is missing for " + varName);
		}
	}

	private int convertToPulses(String value, int lineIndex) {
		String extractedValue = value.replace(PlcUnit.MM.getSuffix(), "").trim();
		return converterMMtoPulse.convertToPulse(Float.parseFloat(extractedValue), lineIndex);
	}

	private boolean createUnitValue(String value) {
		// value of the plc is true for mm and pulses
		return value.endsWith(PlcUnit.MM.getSuffix()) || value.endsWith(PlcUnit.PULSES.getSuffix());
	}

	private boolean conversionNeeded(String value) {
		return value.endsWith(PlcUnit.MM.getSuffix()) || value.endsWith(PlcUnit.MS.getSuffix())
				|| value.endsWith(PlcUnit.PULSES.getSuffix());
	}

	private void findVarTypeAndSendToPlc(String varName, String value, int lineIndex) throws PlcAdaptorException {
		plcProvider.get().write(createVar(varName, value, lineIndex));
	}

	public void setPlcProvider(PlcProvider plcProvider) {
		this.plcProvider = plcProvider;
	}

	public void setPlcVarMapping(IPLCVariableMappping plcVarMapping) {
		this.plcVarMapping = plcVarMapping;
	}

	public void setConverterMMtoPulse(PlcPulseToMMConverterHandler converterMMtoPulse) {
		this.converterMMtoPulse = converterMMtoPulse;
	}

	public void setTypeByPlcVar(Map<String, PLC_TYPE> typeByPlcVar) {
		this.typeByPlcVar.putAll(typeByPlcVar);
	}
}
