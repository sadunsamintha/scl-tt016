package com.sicpa.standard.sasscl.devices.plc.variable.descriptor;

import javax.swing.JComponent;

import com.sicpa.standard.client.common.descriptor.validator.ValidatorException;
import com.sicpa.standard.sasscl.devices.plc.variable.renderer.PlcIntegerVariableRenderer;
import com.sicpa.standard.sasscl.messages.MessageEventKey;

public class PlcIntegerVariableDescriptor extends PlcVariableDescriptor<Integer> {
	protected int min;
	protected int max;

	public void setMin(final int min) {
		this.min = min;
	}

	public void setMax(final int max) {
		this.max = max;
	}

	@Override
	public void validate() throws ValidatorException {

		Integer value = variable.getValue();
		if (value == null) {
			throw new ValidatorException(MessageEventKey.PLC.VALIDATOR_NULL, variable.getVariableName(),
					variable.getVariableName());
		}

		if (min > value || value > max) {
			throw new ValidatorException(MessageEventKey.PLC.VALIDATOR_RANGE, variable.getVariableName(),
					variable.getVariableName(), value, min, max);
		}
	}

	public int getMin() {
		return min;
	}

	public int getMax() {
		return max;
	}

	@Override
	public JComponent getRenderer() {
		if (renderer == null) {
			renderer = new PlcIntegerVariableRenderer(this);
		}
		return renderer;
	}

	@Override
	public PlcIntegerVariableDescriptor clone() {
		PlcIntegerVariableDescriptor descriptor = new PlcIntegerVariableDescriptor();
		descriptor.plcProvider = plcProvider;
		descriptor.editablePlcVariables = editablePlcVariables;
		descriptor.variable = variable;

		descriptor.min = min;
		descriptor.max = max;
		return descriptor;
	}
}
