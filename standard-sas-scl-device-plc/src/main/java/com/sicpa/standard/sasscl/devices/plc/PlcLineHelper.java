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
	private static final int LINE_INDEX_POSITION = ".com.stLine[1]".length();
	private static final String LINE_PREFIX = ".com.stLine[";
	private static final String LINE_TYPE_VARIABLE_SUFFIX = "Type";

	public static boolean isLineVariable(String plcVarName) {
		return plcVarName.startsWith(LINE_PREFIX);
	}

	public static boolean isLineTypeVariable(String plcVarName) {
		return plcVarName.startsWith(LINE_PREFIX) && plcVarName.endsWith(LINE_TYPE_VARIABLE_SUFFIX);
	}

	public static List<String> getLinesVariableName(String varName) {
		List<String> res = new ArrayList<>();
		for (int i : lineIndexes) {
			res.add(varName.replace(LINE_INDEX_PLACEHOLDER, "" + i));
		}
		return res;
	}

	public static void addLineIndex(int index) {
		synchronized (lineIndexes) {
			lineIndexes.add(index);
		}
	}

	public static List<Integer> getLineIndexes() {
		synchronized (lineIndexes) {
			return new ArrayList<>(lineIndexes);
		}
	}

	public static void resetLineIndex() {
		synchronized (lineIndexes) {
			lineIndexes.clear();
		}
	}

	public static int getLineIndex(String plcVarName) {
		plcVarName = plcVarName.substring(0, LINE_INDEX_POSITION);
		plcVarName = plcVarName.substring(LINE_INDEX_POSITION - 2, LINE_INDEX_POSITION - 1);

		return Integer.parseInt(plcVarName);
	}

	public static String replaceLinePlaceholder(String varName, int lineIndex) {
		return varName.replace(LINE_INDEX_PLACEHOLDER, lineIndex + "");
	}

	public static String replaceLineIndexWithPlaceHolder(String varName) {
		return varName.substring(0, LINE_INDEX_POSITION -2)
				+ LINE_INDEX_PLACEHOLDER + "]"
				+ varName.substring(LINE_INDEX_POSITION);
	}

	/**
	 * clone the plc var, replacing the placeholder with the given lineIndex
	 */
	public static IPlcVariable<?> clone(IPlcVariable<?> source, int lineIndex) {
		return PlcVariable
				.create(replaceLinePlaceholder(source.getVariableName(), lineIndex), source.getVariableType());
	}

	/**
	 * transform a group of template var to actual var (#x replace by the line index)
	 */
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
