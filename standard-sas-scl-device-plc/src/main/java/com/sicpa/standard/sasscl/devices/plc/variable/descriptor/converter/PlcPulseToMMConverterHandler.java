package com.sicpa.standard.sasscl.devices.plc.variable.descriptor.converter;

import java.util.HashMap;
import java.util.Map;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.event.PulseConversionParamChangedEvent;

public class PlcPulseToMMConverterHandler {

	private String encoderResolutionVarName;
	private String shapeDiameterVarName;
	private String encoderModFolEvalVarName;

	private final Map<Integer, PlcPulseToMMConverter> converters = new HashMap<>();

	public int convertToPulse(float mm, int lineIndex) {
		return converters.get(lineIndex).convertToPulse(mm);
	}

	public int convertToMMint(int pulses, int lineIndex) {
		return converters.get(lineIndex).convertToMMint(pulses);
	}

	public float convertToMMfloat(int pulses, int lineIndex) {
		return converters.get(lineIndex).convertToMMfloat(pulses);
	}

	@Subscribe
	public void handlePulseConversionParamChanged(PulseConversionParamChangedEvent evt) {
		int line = evt.getLine();
		PlcPulseToMMConverter converter = converters.get(line);
		if (converter == null) {
			converter = createConverter();
			converters.put(line, converter);
		}
		fillConverter(converter, evt);

		if (isConverterReady(converter)) {
			converter.computeCoef();
		}
	}

	private PlcPulseToMMConverter createConverter() {
		return new PlcPulseToMMConverter();
	}

	private void fillConverter(PlcPulseToMMConverter converter, PulseConversionParamChangedEvent evt) {
		if (isEncoderResolutionParam(evt.getParamName())) {
			converter.setEncoderResolution(evt.getValue());
		} else if (isShapeDiameter(evt.getParamName())) {
			converter.setShapeDiameterValue(evt.getValue());
		} else if (isEncoderModFolEval(evt.getParamName())) {
			converter.setEncoderModFoldEval(evt.getValue());
		}
	}

	private boolean isConverterReady(PlcPulseToMMConverter converter) {
		return converter.getEncoderResolution() > 0 && converter.getShapeDiameterValue() > 0
				&& converter.getEncoderModFoldEval() > 0;
	}

	private boolean isEncoderResolutionParam(String param) {
		return param.endsWith(encoderResolutionVarName);
	}

	private boolean isShapeDiameter(String param) {
		return param.endsWith(shapeDiameterVarName);
	}

	private boolean isEncoderModFolEval(String param) {
		return param.endsWith(encoderModFolEvalVarName);
	}

	public void setEncoderModFolEvalVarName(String encoderModFolEvalVarName) {
		this.encoderModFolEvalVarName = encoderModFolEvalVarName;
	}

	public void setShapeDiameterVarName(String shapeDiameterVarName) {
		this.shapeDiameterVarName = shapeDiameterVarName;
	}

	public void setEncoderResolutionVarName(String encoderResolutionVarName) {
		this.encoderResolutionVarName = encoderResolutionVarName;
	}
}
