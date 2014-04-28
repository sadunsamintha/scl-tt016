package com.sicpa.standard.sasscl.devices.plc;

import java.util.ArrayList;
import java.util.List;

public abstract class PlcListenerAdaptor implements IPlcListener {

	protected final List<String> listeningPlcVariableNames = new ArrayList<String>();

	public void setListeningVariables(List<String> plcVariableNames) {
		listeningPlcVariableNames.addAll(plcVariableNames);
	}

	@Override
	public List<String> getListeningVariables() {
		return listeningPlcVariableNames;
	}

}
