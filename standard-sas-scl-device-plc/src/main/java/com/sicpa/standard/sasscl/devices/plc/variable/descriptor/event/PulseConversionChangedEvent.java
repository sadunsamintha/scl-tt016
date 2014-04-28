package com.sicpa.standard.sasscl.devices.plc.variable.descriptor.event;

import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.converter.IPulseToMMConverter;

public class PulseConversionChangedEvent {

	protected String line;
	protected IPulseToMMConverter converter;

	public PulseConversionChangedEvent(String line, IPulseToMMConverter converter) {
		this.line = line;
		this.converter = converter;
	}

	public String getLine() {
		return line;
	}

	public IPulseToMMConverter getConverter() {
		return converter;
	}

}
