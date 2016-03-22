package com.sicpa.standard.sasscl.devices.plc.variable.descriptor;

import javax.swing.JComponent;

import com.sicpa.standard.sasscl.devices.plc.variable.renderer.PlcShortVariableRenderer;

public class PlcShortVariableDescriptor extends PlcVariableDescriptor {

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
		descriptor.listeners.addAll(listeners);
		descriptor.value = value;
		return descriptor;
	}

}
