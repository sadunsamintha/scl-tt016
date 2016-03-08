package com.sicpa.standard.sasscl.devices.plc;

import java.util.ArrayList;
import java.util.List;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.ioc.BeanProvider;
import com.sicpa.standard.plc.value.IPlcVariable;
import com.sicpa.standard.plc.value.PlcVariable;
import com.sicpa.standard.sasscl.devices.plc.variable.PlcVariableGroup;
import com.sicpa.standard.sasscl.devices.plc.variable.PlcVariableGroupEvent;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.PlcVariableDescriptor;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.converter.PlcPulseToMMConverterHandler;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.unit.PlcUnit;

public class PlcLineHelper {

	private static final List<Integer> lineIndexes = new ArrayList<>();
	public static String lineIndexPlaceholder = "#x";

	public static List<String> getLinesVariableName(String varName) {
		List<String> res = new ArrayList<>();
		for (int i : lineIndexes) {
			res.add(varName.replace("#x", "" + i));
		}
		return res;
	}

	public static void addLineIndex(int index) {
		lineIndexes.add(index);
	}

	public static int getLineIndex(String param) {
		int lineIndexPosition = ".com.stLine[1]".length();
		return Integer.parseInt(param.substring(0, lineIndexPosition));
	}

	public static String replaceLinePlaceholder(String varName, int lineIndex) {
		return varName.replace(lineIndexPlaceholder, lineIndex + "");

	}

	public static IPlcVariable<?> clone(IPlcVariable<?> source, int lineIndex) {
		return PlcVariable.create(PlcLineHelper.replaceLinePlaceholder(source.getVariableName(), lineIndex),
				source.getVariableType());
	}

	// transform a group of template var to actual var (#x replace by the line index)
	public static List<PlcVariableGroup> replaceLinePlaceholder(List<PlcVariableGroup> groups, int index) {
		List<PlcVariableGroup> res = new ArrayList<>();
		for (int i = 0; i < groups.size(); i++) {
			PlcVariableGroup newGrp = new PlcVariableGroup();
			res.add(newGrp);
			newGrp.setDescription(groups.get(i).getDescription());
			for (PlcVariableDescriptor desc : groups.get(i).getPlcVars()) {
				PlcVariableDescriptor newDesc = desc.clone();
				newDesc.setLineIndex(index);
				newDesc.setVarName(PlcLineHelper.replaceLinePlaceholder(desc.getVarName(), index));
				newGrp.addDescriptor(newDesc);
			}
		}
		return res;
	}

	

}
