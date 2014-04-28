package com.sicpa.standard.sasscl.devices.plc.variable.descriptor;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.event.PulseConversionParamChangedEvent;

public class PlcVariablePulseParamDescriptor extends PlcIntegerVariableDescriptor {

	@Override
	protected void fireValueChanged() {
		super.fireValueChanged();
		if (getVariable().getValue() == null){
			logger.error("variable {} not defined", getVarName());
			return;
		}
		EventBusService.post(new PulseConversionParamChangedEvent(
					getVarName(), getVariable().getValue().intValue()));
	}

	@Override
	public PlcVariablePulseParamDescriptor clone() {
		PlcVariablePulseParamDescriptor descriptor = new PlcVariablePulseParamDescriptor();
		descriptor.plcProvider = plcProvider;
		descriptor.editablePlcVariables = editablePlcVariables;
		descriptor.variable = variable;

		descriptor.min = min;
		descriptor.max = max;
		return descriptor;
	}

}
