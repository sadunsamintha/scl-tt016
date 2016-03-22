package com.sicpa.standard.sasscl.devices.plc.variable.renderer;

import javax.swing.SpinnerNumberModel;

import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.PlcShortVariableDescriptor;

@SuppressWarnings("serial")
public class PlcShortVariableRenderer extends AbstractPlcNumberVariableRenderer<Short> {

	public PlcShortVariableRenderer(PlcShortVariableDescriptor desc) {
		super(desc);
	}

	@Override
	protected SpinnerNumberModel createSpinnerNumberModel() {
		return new SpinnerNumberModel(new Short((short) 0), new Short((short) 0), new Short(Short.MAX_VALUE),
				new Short((short) 1));
	}

	@Override
	protected Short parseValue(String value) {
		return Short.parseShort(value);
	}

}
