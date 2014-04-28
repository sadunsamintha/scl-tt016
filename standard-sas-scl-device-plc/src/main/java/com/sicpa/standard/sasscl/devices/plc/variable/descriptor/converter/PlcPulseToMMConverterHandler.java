package com.sicpa.standard.sasscl.devices.plc.variable.descriptor.converter;

import java.util.HashMap;
import java.util.Map;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.devices.plc.PlcVariableMap;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.event.PulseConversionChangedEvent;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.event.PulseConversionParamChangedEvent;

public class PlcPulseToMMConverterHandler {

	protected final Map<String, PlcPulseToMMConverter> converters = new HashMap<String, PlcPulseToMMConverter>();

	@Subscribe
	public void handlePulseConversionParamChanged(PulseConversionParamChangedEvent evt) {

		String line = PlcVariableMap.getLineIndex(evt.getParamName());
		PlcPulseToMMConverter converter = converters.get(line);
		if (converter == null) {
			converter = createConverter();
			converters.put(line, converter);
		}
		fillConverter(converter, evt);

		if (isConverterReady(converter)) {
			EventBusService.post(new PulseConversionChangedEvent(line, converter));
		}
	}

	protected PlcPulseToMMConverter createConverter() {
		return new PlcPulseToMMConverter();
	}

	protected void fillConverter(PlcPulseToMMConverter converter, PulseConversionParamChangedEvent evt) {
		if (isEncoderResolutionParam(evt.getParamName())) {
			converter.setEncoderResolution(evt.getValue());
		} else if (isShapeDiameter(evt.getParamName())) {
			converter.setShapeDiameterValue(evt.getValue());
		} else if (isEncoderModFolEval(evt.getParamName())) {
			converter.setEncoderModFoldEval(evt.getValue());
		}
	}

	protected boolean isConverterReady(PlcPulseToMMConverter converter) {
		return converter.getEncoderResolution() > 0 && converter.getShapeDiameterValue() > 0
				&& converter.getEncoderModFoldEval() > 0;
	}

	protected String encoderResolutionVarName;
	protected String shapeDiameterVarName;
	protected String encoderModFolEvalVarName;

	protected boolean isEncoderResolutionParam(String param) {
		return param.endsWith(encoderResolutionVarName);
	}

	protected boolean isShapeDiameter(String param) {
		return param.endsWith(shapeDiameterVarName);
	}

	protected boolean isEncoderModFolEval(String param) {
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
