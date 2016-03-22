package com.sicpa.standard.sasscl.devices.plc.variable.descriptor;

import javax.swing.JComponent;

import com.sicpa.standard.sasscl.devices.plc.variable.renderer.PlcBooleanVariableRenderer;

public class PlcBooleanVariableDescriptor extends PlcVariableDescriptor {

	@Override
	public JComponent getRenderer() {
		if (renderer == null) {
			renderer = new PlcBooleanVariableRenderer(this);
		}
		return renderer;
	}

	@Override
	public PlcVariableDescriptor clone() {
		PlcBooleanVariableDescriptor descriptor = new PlcBooleanVariableDescriptor();
		descriptor.listeners.addAll(listeners);
		descriptor.value=value;
		return descriptor;
	}

}
