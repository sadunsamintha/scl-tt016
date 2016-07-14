package com.sicpa.standard.sasscl.devices.camera.jobconfig.parameters.type.impl;

import com.sicpa.standard.sasscl.devices.camera.jobconfig.parameters.IJobParameterWriter;


public class ValueResetCameraJobParam extends AliasCameraJobParameter<String> {

	@Override
	public void writeValue(IJobParameterWriter writer) {
		writer.writeAliasValue(getAlias(), "1");
		writer.writeAliasValue(getAlias(), "0");
	}
}
