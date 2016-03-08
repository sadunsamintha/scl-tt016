package com.sicpa.standard.sasscl.devices.plc.variable.descriptor;

import javax.swing.JComponent;

import com.sicpa.standard.sasscl.devices.plc.variable.renderer.PlcByteVariableRenderer;

public class PlcByteVariableDescriptor extends PlcVariableDescriptor {

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
		descriptor.listeners.addAll(listeners);
		descriptor.value = value;
		return descriptor;
	}
}
