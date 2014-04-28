package com.sicpa.standard.sasscl.devices.plc.variable;

import com.sicpa.standard.client.common.utils.ConfigUtils;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.PlcVariableDescriptor;
import com.sicpa.standard.sasscl.monitoring.mbean.sas.ISaveRemotlyUpdatedBeanTask;
import com.sicpa.standard.sasscl.provider.impl.PlcProvider;

public class SavePlcVarRemotlyChanged implements ISaveRemotlyUpdatedBeanTask {

	protected PlcProvider plcProvider;

	@Override
	public void save(String beanName, Object newObject, Object oldObject) throws Exception {

		// when plc var are being changed remotely send them to the plc
		// then save the values
		EditablePlcVariables editablePlcVariables = (EditablePlcVariables) newObject;
		for (PlcVariableGroup grp : editablePlcVariables.getGroups()) {
			for (PlcVariableDescriptor<?> var : grp.getPlcVars()) {
				var.setPlcProvider(plcProvider);
				var.sendValueToPlc();
			}
		}
		ConfigUtils.save(editablePlcVariables.getPlcValues());

	}

	public void setPlcProvider(PlcProvider plcProvider) {
		this.plcProvider = plcProvider;
	}

	public PlcProvider getPlcProvider() {
		return plcProvider;
	}

}
