/**
 * Author	: CWong
 * Date		: Sep 6, 2010
 *
 * Copyright (c) 2010 SICPA Security Solutions, all rights reserved.
 *
 */
package com.sicpa.standard.sasscl.devices.plc.simulator;

import com.sicpa.standard.plc.controller.model.IPlcModel;
import com.sicpa.standard.plc.value.IPlcVariable;
import com.sicpa.standard.sasscl.devices.plc.PlcMessageDescriptor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Model to setup plc simulator
 * 
 * @see PlcSimulatorController
 * 
 * 
 * @author CWong
 * 
 */
public class PlcSimulatorConfig implements IPlcModel, Serializable {

	private static final long serialVersionUID = 7439631141911483712L;

	/**
	 * to configure how the notification should be sent to listeners
	 */
	public List<PlcSimulatorNotificationConfig> notificationVariables;

	/**
	 * to register a list of variables in PLC
	 */
	public List<IPlcVariable<?>> plcVariables;

	public PlcSimulatorConfig() {
		this.notificationVariables = new ArrayList<PlcSimulatorNotificationConfig>();
		this.plcVariables = new ArrayList<IPlcVariable<?>>();
	}

	// setters and getters

	public List<IPlcVariable<?>> getPlcVariables() {
		return this.plcVariables;
	}

	public void setPlcVariables(List<IPlcVariable<?>> plcVariables) {
		this.plcVariables = plcVariables;
	}

	/**
	 * 
	 * add list of PLC variables into the existing list
	 * 
	 * @param plcVariables
	 */
	public void addPlcVariables(List<IPlcVariable<?>> plcVariables) {
		if (this.plcVariables != null) {
			this.plcVariables.addAll(plcVariables);
		}
	}

	/**
	 * add PLC variable into the existing list
	 * 
	 * @param plcVariable
	 */
	public void addPlcVariable(IPlcVariable<?> plcVariable) {
		if (this.plcVariables != null) {
			this.plcVariables.add(plcVariable);
		}
	}

	/**
	 * add PLC variables from an array of PLCMessageDescriptor
	 * 
	 * @param plcMsgDescriptors
	 */
	public void addPlcVariablesFromMsgDescriptors(List<PlcMessageDescriptor> plcMsgDescriptors) {

		if (this.plcVariables == null || plcMsgDescriptors == null) {
			return;
		}
		for (PlcMessageDescriptor msgDescriptor : plcMsgDescriptors) {
			this.plcVariables.add(msgDescriptor.getPlcVariable());
		}
	}

	public void setPlcVariablesFromMsgDescriptors(List<PlcMessageDescriptor> plcMsgDescriptors) {
		this.addPlcVariablesFromMsgDescriptors(plcMsgDescriptors);
	}

	public List<PlcSimulatorNotificationConfig> getNotificationVariables() {
		return this.notificationVariables;
	}

	public void setNotificationVariables(List<PlcSimulatorNotificationConfig> notificationVariables) {

		if (null == this.notificationVariables) {
			this.notificationVariables = notificationVariables;
			return;
		}

		this.notificationVariables.addAll(notificationVariables);
	}

	@Override
	public boolean equals(Object o) {

		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		PlcSimulatorConfig that = (PlcSimulatorConfig) o;

		if (notificationVariables != null ? !notificationVariables.equals(that.notificationVariables)
				: that.notificationVariables != null)
			return false;
		if (plcVariables != null ? !plcVariables.equals(that.plcVariables) : that.plcVariables != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {

		int result = notificationVariables != null ? notificationVariables.hashCode() : 0;
		result = 31 * result + (plcVariables != null ? plcVariables.hashCode() : 0);
		return result;
	}
}
