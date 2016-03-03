package com.sicpa.standard.sasscl.devices.plc.variable.descriptor;

import javax.swing.JComponent;

import com.sicpa.standard.client.common.descriptor.validator.ValidatorException;
import com.sicpa.standard.sasscl.devices.plc.variable.renderer.PlcShortVariableRenderer;
import com.sicpa.standard.sasscl.messages.MessageEventKey;

public class PlcShortVariableDescriptor extends PlcVariableDescriptor<Short> {
	protected short min;
	protected short max;

	public void setMin(final short min) {
		this.min = min;
	}

	public void setMax(final short max) {
		this.max = max;
	}

	@Override
	public void validate() throws ValidatorException {
		try {
			Short value = variable.getValue();
			if (value == null) {
				throw new ValidatorException(MessageEventKey.PLC.VALIDATOR_NULL, variable.getVariableName(),
						variable.getVariableName());
			}

			if (this.min > value || value > max) {
				throw new ValidatorException(MessageEventKey.PLC.VALIDATOR_RANGE, variable.getVariableName(),
						variable.getVariableName(), value, min, max);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(variable.getVariableName() + " " + variable.getVariableName() + "--" + variable);
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
			renderer = new PlcShortVariableRenderer(this);
		}
		return renderer;
	}

	@Override
	public PlcShortVariableDescriptor clone() {
		PlcShortVariableDescriptor descriptor = new PlcShortVariableDescriptor();
		descriptor.plcProvider = plcProvider;
		descriptor.editablePlcVariables = editablePlcVariables;
		descriptor.variable = variable;

		descriptor.min = min;
		descriptor.max = max;
		return descriptor;
	}
}
