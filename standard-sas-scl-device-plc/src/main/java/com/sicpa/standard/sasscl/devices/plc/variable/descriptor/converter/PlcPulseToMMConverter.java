package com.sicpa.standard.sasscl.devices.plc.variable.descriptor.converter;

import com.sicpa.standard.client.common.device.plc.converter.DefaultPulseMMConverter;

public class PlcPulseToMMConverter extends DefaultPulseMMConverter {

	private volatile int shapeDiameterValue = 100;
	private volatile int encoderResolution = 5000;
	private volatile int encoderModFoldEval = 4;

	public void computeCoef() {
		setCoef((int) ((encoderResolution * encoderModFoldEval) / (Math.PI * shapeDiameterValue)));
	}

	public void setShapeDiameterValue(int shapeDiameterValue) {
		this.shapeDiameterValue = shapeDiameterValue;
	}

	public void setEncoderResolution(int encoderResolution) {
		this.encoderResolution = encoderResolution;
	}

	public int getEncoderResolution() {
		return encoderResolution;
	}

	public int getShapeDiameterValue() {
		return shapeDiameterValue;
	}

	public int getEncoderModFoldEval() {
		return encoderModFoldEval;
	}

	public void setEncoderModFoldEval(int encoderModFoldEval) {
		this.encoderModFoldEval = encoderModFoldEval;
	}
}
