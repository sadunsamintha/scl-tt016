package com.sicpa.standard.sasscl.devices.plc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * Class to keep PLC variable map
 * 
 */
public class PlcVariableMap {

	protected static List<Integer> lineIndexes = new ArrayList<Integer>();

	public static String lineIndexPlaceholder = "#x";

	protected final static Map<String, String> variableNameMap = new HashMap<String, String>();

	/**
	 * Adds entries from varMap to the static variableMap
	 * 
	 * @param varMap
	 *            holds entries to be added to the static instance
	 */
	public PlcVariableMap(Map<String, String> varMap) {
		addPlcVariables(varMap);
	}

	/**
	 * 
	 * @param key
	 *            plc logical name
	 * @return var name in the plc
	 */
	public static String get(String key) {
		return variableNameMap.get(key);
	}

	/**
	 * adding new entry to PLC variables
	 * 
	 * 
	 * 
	 * @param variableMaps
	 */
	public static void addPlcVariables(Map<String, String> variableMaps) {
		variableNameMap.putAll(variableMaps);
	}

	public static void addPlcVariable(String logical, String nameOnPlc) {
		variableNameMap.put(logical, nameOnPlc);
	}

	public static List<String> getLineVariableName(String key) {
		List<String> res = new ArrayList<String>();
		for (int i : lineIndexes) {
			res.add(key.replace("#x", "" + i));
		}
		return res;
	}

	/**
	 * clear the content of the plc variables
	 */
	public static void clearPlcVariables() {
		variableNameMap.clear();
	}

	public static void addLineIndex(int index) {
		lineIndexes.add(index);
	}

	public static String getLineIndex(String param) {
		int lineIndexPosition = ".com.stLine[1]".length();
		return param.substring(0, lineIndexPosition);
	}
}
