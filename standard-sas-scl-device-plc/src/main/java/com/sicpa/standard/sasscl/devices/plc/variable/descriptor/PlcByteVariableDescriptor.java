package com.sicpa.standard.sasscl.devices.plc.variable.descriptor;

import javax.swing.JComponent;

import com.sicpa.standard.client.common.descriptor.validator.ValidatorException;
import com.sicpa.standard.sasscl.devices.plc.variable.renderer.PlcByteVariableRenderer;
import com.sicpa.standard.sasscl.messages.MessageEventKey;

public class PlcByteVariableDescriptor extends PlcVariableDescriptor<Short> {
	protected byte min;
	protected byte max;

	public void setMin(final byte min) {
		this.min = min;
	}

	public void setMax(final byte max) {
		this.max = max;
	}

	@Override
	public void validate() throws ValidatorException {

		Short value = variable.getValue();
		if (value == null) {
			throw new ValidatorException(MessageEventKey.PLC.VALIDATOR_NULL, variable.getVariableName(),
					variable.getVariableName());
		}

		if (this.min > value || value > max) {
			throw new ValidatorException(MessageEventKey.PLC.VALIDATOR_RANGE, variable.getVariableName(),
					variable.getVariableName(), value, min, max);
		}
	}

	public short getMin() {
		return min;
	}

	public short getMax() {
		return max;
	}

	@Override
	public JComponent getRenderer() {
		if (renderer == null) {
			renderer = new PlcByteVariableRenderer(this);
		}
		return renderer;
	}

	@Override
	public PlcByteVariableDescriptor clone() {
		PlcByteVariableDescriptor descriptor = new PlcByteVariableDescriptor();
		descriptor.plcProvider = plcProvider;
		descriptor.editablePlcVariables = editablePlcVariables;
		descriptor.variable = variable;

		descriptor.min = min;
		descriptor.max = max;
		return descriptor;
	}
}
