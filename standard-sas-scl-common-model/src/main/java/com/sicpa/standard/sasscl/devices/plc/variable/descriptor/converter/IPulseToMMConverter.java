package com.sicpa.standard.sasscl.devices.plc.variable.descriptor.converter;


public interface IPulseToMMConverter {

	/**
	 * @return the pulse value convertedTo mm
	 */
	double convertToMM(int pulseValue);

}
