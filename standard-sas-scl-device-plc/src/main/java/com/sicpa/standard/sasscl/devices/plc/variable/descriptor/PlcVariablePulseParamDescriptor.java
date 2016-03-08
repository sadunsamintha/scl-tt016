package com.sicpa.standard.sasscl.devices.plc.variable.descriptor;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.event.PulseConversionParamChangedEvent;

public class PlcVariablePulseParamDescriptor extends PlcIntegerVariableDescriptor {

	@Override
	protected void fireValueChanged() {
		super.fireValueChanged();
		if (getValue() == null) {
			logger.error("variable {} not defined", getVarName());
			return;
		}
		EventBusService
				.post(new PulseConversionParamChangedEvent(getVarName(), Integer.parseInt(getValue()), lineIndex));
	}

	@Override
	public PlcVariablePulseParamDescriptor clone() {
		PlcVariablePulseParamDescriptor descriptor = new PlcVariablePulseParamDescriptor();
		descriptor.listeners.addAll(listeners);
		descriptor.value = value;
		return descriptor;
	}

}
