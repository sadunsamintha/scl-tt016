package com.sicpa.standard.sasscl.devices.plc.variable.descriptor;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.event.PulseConversionParamChangedEvent;

public class PlcVariablePulseParamDescriptor extends PlcIntegerVariableDescriptor {

	@Override
	public void initValue(String value) {
		super.initValue(value);
		sendPulseConversionChangedEvent();
	}

	private void sendPulseConversionChangedEvent() {
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
