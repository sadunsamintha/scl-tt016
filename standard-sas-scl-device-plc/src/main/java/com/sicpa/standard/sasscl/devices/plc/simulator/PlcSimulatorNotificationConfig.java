/**
 * Author	: YYang
 * Date		: Oct 2, 2010
 *
 * Copyright (c) 2010 SICPA Security Solutions, all rights reserved.
 *
 */
package com.sicpa.standard.sasscl.devices.plc.simulator;

import com.sicpa.standard.plc.value.IPlcVariable;
import com.sicpa.standard.plc.value.PlcVariable;

/**
 * Notification configuration for PLC simulator
 * 
 */
public class PlcSimulatorNotificationConfig {

	/**
	 * how frequent the listener get the notification
	 * 
	 * unit is in milliseconds
	 * 
	 */
	protected int notificationInterval;

	/**
	 * variable name
	 */
	protected String varName;

	/**
	 * initial value
	 */
	protected Object initialValue;

	/**
	 * define how the notification is going to change
	 */
	protected PlcSimulatorNotificationValuePattern valuePattern = PlcSimulatorNotificationValuePattern.STATIC;

	/**
	 * this value is required if the pattern is set to INCREMENTAL
	 * 
	 * only for integer variable
	 */
	protected int incrementalValue;

	/**
	 * this value is required if the pattern is set to RANDOM
	 * 
	 * only for integer variable
	 * 
	 * minimum value of the value
	 * 
	 */
	protected int minValue;

	/**
	 * this value is required if the pattern is set to RANDOM
	 * 
	 * only for integer variable
	 * 
	 * maximum value of the value
	 * 
	 */
	protected int maxValue;

	public PlcSimulatorNotificationConfig() {
	}

	/**
	 * 
	 * @param variable
	 * @param notificationInterval
	 * @param value
	 */
	public PlcSimulatorNotificationConfig(String varName, Object initialValue, int notificationInterval) {
		this.notificationInterval = notificationInterval;
		this.initialValue = initialValue;
		this.varName = varName;
	}

	/**
	 * 
	 * create instance of PLCSimulatorNotificationVariable with incremental pattern
	 * 
	 * @param variableName
	 * @param initialValue
	 * @param incrementalValue
	 * @param notificationInterval
	 * @return
	 */
	public static PlcSimulatorNotificationConfig createIncrementalPatternVar(String variableName, int initialValue,
			int incrementalValue, int notificationInterval) {
		PlcSimulatorNotificationConfig var = new PlcSimulatorNotificationConfig(variableName, initialValue,
				notificationInterval);
		var.setIncrementalValue(incrementalValue);
		var.setValuePattern(PlcSimulatorNotificationValuePattern.INCREMENTAL);
		return var;
	}

	/**
	 * 
	 * create instance of PLCSimulatorNotificationVariable with random pattern for integer variable
	 * 
	 * @param plcVariable
	 * @param minValue
	 * @param maxValue
	 * @param notificationInterval
	 * @return
	 */
	public static PlcSimulatorNotificationConfig createRandomPatternIntVar(String variableName, int minValue,
			int maxValue, int notificationInterval) {
		PlcSimulatorNotificationConfig var = new PlcSimulatorNotificationConfig(variableName, 0, notificationInterval);
		var.setValuePattern(PlcSimulatorNotificationValuePattern.RANDOM);
		var.setMinValue(minValue);
		var.setMaxValue(maxValue);
		return var;
	}

	/**
	 * 
	 * create instance of PLCSimulatorNotificationVariable with random pattern for boolean variable
	 * 
	 * @param variableName
	 * @param initialValue
	 * @param notificationInterval
	 * @return
	 */
	public static PlcSimulatorNotificationConfig createRandomPatternBooleanVar(String variableName,
			int notificationInterval) {
		PlcSimulatorNotificationConfig var = new PlcSimulatorNotificationConfig(variableName, false,
				notificationInterval);
		var.setValuePattern(PlcSimulatorNotificationValuePattern.RANDOM);
		return var;
	}
	
	public static PlcSimulatorNotificationConfig createStaticPatternBooleanVar(String variableName,
			int notificationInterval,boolean value) {
		PlcSimulatorNotificationConfig var = new PlcSimulatorNotificationConfig(variableName, value,
				notificationInterval);
		var.setValuePattern(PlcSimulatorNotificationValuePattern.STATIC);
		return var;
	}

	/**
	 * to form a PLC variable based on variable name and value
	 * 
	 * @param varName
	 * @param initialValue
	 * @return
	 */
	public static IPlcVariable<?> getPlcVariable(String varName, Object initialValue) {
		if (initialValue instanceof Boolean) {
			IPlcVariable<Boolean> booleanVar = PlcVariable.createBooleanVar(varName);
			booleanVar.setValue((Boolean) initialValue);
			return booleanVar;
		} else if (initialValue instanceof Integer) {
			IPlcVariable<Integer> integerVar = PlcVariable.createInt32Var(varName);
			integerVar.setValue((Integer) initialValue);
			return integerVar;
		}
		return null;
	}

	public int getNotificationInterval() {
		return this.notificationInterval;
	}

	public void setNotificationInterval(int notificationInterval) {
		this.notificationInterval = notificationInterval;
	}

	public PlcSimulatorNotificationValuePattern getValuePattern() {
		return this.valuePattern;
	}

	public void setValuePattern(PlcSimulatorNotificationValuePattern valuePattern) {
		this.valuePattern = valuePattern;
	}

	public int getIncrementalValue() {
		return this.incrementalValue;
	}

	public void setIncrementalValue(int incrementalValue) {
		this.incrementalValue = incrementalValue;
	}

	public int getMinValue() {
		return this.minValue;
	}

	public void setMinValue(int minValue) {
		this.minValue = minValue;
	}

	public int getMaxValue() {
		return this.maxValue;
	}

	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}

	public String getVarName() {
		return this.varName;
	}

	public void setVarName(String varName) {
		this.varName = varName;
	}

	public Object getInitialValue() {
		return this.initialValue;
	}

	public void setInitialValue(Object initialValue) {
		this.initialValue = initialValue;
	}

}
