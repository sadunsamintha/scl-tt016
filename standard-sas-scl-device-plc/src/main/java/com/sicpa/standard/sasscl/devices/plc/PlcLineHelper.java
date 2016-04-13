package com.sicpa.standard.sasscl.devices.plc;

import java.util.ArrayList;
import java.util.List;

import com.sicpa.standard.plc.value.IPlcVariable;
import com.sicpa.standard.plc.value.PlcVariable;
import com.sicpa.standard.sasscl.devices.plc.variable.PlcVariableGroup;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.PlcVariableDescriptor;

public class PlcLineHelper {

	private static final List<Integer> lineIndexes = new ArrayList<>();
	public static final String LINE_INDEX_PLACEHOLDER = "#x";
	private static final int lineIndexPosition = ".com.stLine[1]".length();

	public static List<String> getLinesVariableName(String varName) {
		List<String> res = new ArrayList<>();
		for (int i : lineIndexes) {
			res.add(varName.replace(LINE_INDEX_PLACEHOLDER, "" + i));
		}
		return res;
	}

	public static void addLineIndex(int index) {
		lineIndexes.add(index);
	}

	public static List<Integer> getLineIndexes() {
		return lineIndexes;
	}

	public static int getLineIndex(String param) {
		param = param.substring(0, lineIndexPosition);
		param = param.substring(lineIndexPosition - 2, lineIndexPosition - 1);

		return Integer.parseInt(param);
	}

	public static String replaceLinePlaceholder(String varName, int lineIndex) {
		return varName.replace(LINE_INDEX_PLACEHOLDER, lineIndex + "");

	}

	public static IPlcVariable<?> clone(IPlcVariable<?> source, int lineIndex) {
		return PlcVariable.create(PlcLineHelper.replaceLinePlaceholder(source.getVariableName(), lineIndex), source.getVariableType());
	}

	// transform a group of template var to actual var (#x replace by the line
	// index)
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
