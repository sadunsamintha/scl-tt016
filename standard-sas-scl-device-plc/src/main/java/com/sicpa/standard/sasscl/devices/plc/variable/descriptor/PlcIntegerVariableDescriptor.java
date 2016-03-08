package com.sicpa.standard.sasscl.devices.plc.variable.descriptor;

import javax.swing.JComponent;

import com.sicpa.standard.sasscl.devices.plc.variable.renderer.PlcIntegerVariableRenderer;

public class PlcIntegerVariableDescriptor extends PlcVariableDescriptor {

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
		descriptor.varName = varName;
		descriptor.listeners.addAll(listeners);
		descriptor.value = value;
		return descriptor;
	}

}
