package com.sicpa.standard.sasscl.devices.plc;

import java.util.ArrayList;
import java.util.List;

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

	public static String getLineIndex(String param) {
		int lineIndexPosition = ".com.stLine[1]".length();
		return param.substring(0, lineIndexPosition);
	}
}
