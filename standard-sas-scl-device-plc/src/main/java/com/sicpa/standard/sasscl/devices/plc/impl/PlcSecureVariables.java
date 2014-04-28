package com.sicpa.standard.sasscl.devices.plc.impl;

import java.util.List;

import com.sicpa.standard.sasscl.devices.plc.PlcVariableMap;

public enum PlcSecureVariables {
	
//	--------------- NOTIFICATIONS
	NTF_USER_ID_AUTHENTICATED,
	NTF_USER_PROFILE_AUTHENTICATED,
	NTF_USER_AUTHENTICATION,
	NTF_USER_DATABASE_VERSION;
	
	/**
	 * 
	 * return name of the PLC variable
	 * 
	 * @return
	 */
	public String getVariableName() {
		return PlcVariableMap.get(this.name());
	}
	
	public static PlcSecureVariables getKey(String plcVarName) {
		for (PlcSecureVariables var : PlcSecureVariables.values()){
			if (var.getVariableName().equals(plcVarName)) {
				return var;
			}
		}
		return null;
	}

	
	public List<String> getLineVariableNames() {
		return PlcVariableMap.getLineVariableName(getVariableName());
	}

}
