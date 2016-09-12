package com.sicpa.standard.sasscl.devices.plc.variable.renderer;

import javax.swing.SpinnerNumberModel;

import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.PlcVariableDescriptor;

@SuppressWarnings("serial")
public class PlcByteVariableRenderer extends AbstractPlcNumberVariableRenderer<Byte> {

	public PlcByteVariableRenderer(PlcVariableDescriptor desc) {
		super(desc);
	}

	@Override
	protected SpinnerNumberModel createSpinnerNumberModel() {
		return new SpinnerNumberModel(new Byte((byte) 0), new Byte((byte) 0), new Byte(Byte.MAX_VALUE), new Byte((byte) 1));
	}

	@Override
	protected Byte parseValue(String value) {
		return Byte.parseByte(value);
	}

}
