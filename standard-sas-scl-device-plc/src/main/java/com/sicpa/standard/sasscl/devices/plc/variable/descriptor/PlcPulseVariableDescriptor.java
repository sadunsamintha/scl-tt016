package com.sicpa.standard.sasscl.devices.plc.variable.descriptor;

import javax.swing.JComponent;

import com.sicpa.standard.sasscl.common.log.OperatorLogger;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.unit.PlcUnit;
import com.sicpa.standard.sasscl.devices.plc.variable.renderer.PlcPulseUnitRenderer;

public class PlcPulseVariableDescriptor extends PlcIntegerVariableDescriptor {

	private PlcUnit currentUnit;

	public PlcPulseVariableDescriptor() {
	}

	@Override
	public void setValue(String value) {
		if (this.value.equals(value)) {
			return;
		}
		this.value = value;
		OperatorLogger.log("PLC Variables - {} = {}", new Object[] { getVarName(), value });
		currentUnit = PlcUnit.extractUnitFromValue(value);
		fireValueChanged();
		saveAndSendValueToPlc();
	}

	public void initValue(String value) {
		this.value = value;
		currentUnit = PlcUnit.extractUnitFromValue(value);
	}

	@Override
	public JComponent getRenderer() {
		if (renderer == null) {
			renderer = new PlcPulseUnitRenderer(this);
		}
		return renderer;
	}

	@Override
	public PlcPulseVariableDescriptor clone() {
		PlcPulseVariableDescriptor descriptor = new PlcPulseVariableDescriptor();
		descriptor.currentUnit = currentUnit;
		descriptor.listeners.addAll(listeners);
		descriptor.value = value;
		return descriptor;
	}

	public PlcUnit getCurrentUnit() {
		return currentUnit;
	}
}
