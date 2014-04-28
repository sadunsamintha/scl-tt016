package com.sicpa.standard.sasscl.devices.plc.variable.descriptor.converter;

public class PlcPulseToMMConverter implements IPulseToMMConverter {

	protected int shapeDiameterValue;
	protected int encoderResolution;
	protected int encoderModFoldEval;

	public PlcPulseToMMConverter() {
	}

	@Override
	public double convertToMM(final int pulseValue) {
		return Math.PI * shapeDiameterValue * (1f * pulseValue / encoderResolution) / encoderModFoldEval;
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
