package com.sicpa.standard.sasscl.devices.plc.variable.descriptor;

import javax.swing.JComponent;

import com.sicpa.standard.client.common.descriptor.validator.ValidatorException;
import com.sicpa.standard.sasscl.devices.plc.variable.renderer.PlcBooleanVariableRenderer;

public class PlcBooleanVariableDescriptor extends PlcVariableDescriptor<Boolean> {

	@Override
	public void validate() throws ValidatorException {

	}

	@Override
	public JComponent getRenderer() {
		if (renderer == null) {
			renderer = new PlcBooleanVariableRenderer(this);
		}
		return renderer;
	}

	@Override
	public PlcVariableDescriptor<Boolean> clone() {
		PlcBooleanVariableDescriptor descriptor=new PlcBooleanVariableDescriptor();
		descriptor.plcProvider=plcProvider;
		descriptor.editablePlcVariables=editablePlcVariables;
		descriptor.variable=variable;
		return descriptor;
	}
}
