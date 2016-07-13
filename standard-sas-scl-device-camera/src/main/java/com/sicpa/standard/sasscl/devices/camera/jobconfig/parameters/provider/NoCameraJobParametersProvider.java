package com.sicpa.standard.sasscl.devices.camera.jobconfig.parameters.provider;

import java.util.Collection;
import java.util.Collections;

import com.sicpa.standard.sasscl.devices.camera.jobconfig.parameters.type.ICameraJobParameter;

public class NoCameraJobParametersProvider implements ICameraJobParametersProvider {

	@Override
	public Collection<ICameraJobParameter> getParameterList() {
		return Collections.emptyList();
	}

}
