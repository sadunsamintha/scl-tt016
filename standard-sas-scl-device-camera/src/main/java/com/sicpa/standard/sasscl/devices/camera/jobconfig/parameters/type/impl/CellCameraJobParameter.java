package com.sicpa.standard.sasscl.devices.camera.jobconfig.parameters.type.impl;

import com.sicpa.standard.sasscl.devices.camera.jobconfig.parameters.IJobParameterWriter;

public class CellCameraJobParameter extends AliasCameraJobParameter<String> {

	@Override
	public void writeValue(IJobParameterWriter writer) {
		String parameterName = getAlias();
		writer.writeAliasValue(parameterName, getParameterValue());
	}

}
