package com.sicpa.standard.sasscl.devices.plc.variable.renderer;

import javax.swing.SpinnerNumberModel;

import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.PlcIntegerVariableDescriptor;

@SuppressWarnings("serial")
public class PlcIntegerVariableRenderer extends AbstractPlcNumberVariableRenderer<Integer> {

	public PlcIntegerVariableRenderer(PlcIntegerVariableDescriptor desc) {
		super(desc);
	}

	@Override
	protected SpinnerNumberModel createSpinnerNumberModel() {
		return new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1);
	}

	@Override
	protected Integer parseValue(String value) {
		return Integer.parseInt(value);
	}

	@Override
	public void valueChanged() {
	}
}
