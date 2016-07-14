package com.sicpa.standard.sasscl.devices.camera.jobconfig.parameters.type.impl;

import com.sicpa.standard.sasscl.devices.camera.jobconfig.parameters.IJobParameterWriter;


public class IntCameraJobParameter extends AliasCameraJobParameter<Integer> {

	public void writeValue(IJobParameterWriter writer) {
		String parameterName = getAlias();
		writer.writeIntValue(parameterName, getParameterValue());
	}
}
