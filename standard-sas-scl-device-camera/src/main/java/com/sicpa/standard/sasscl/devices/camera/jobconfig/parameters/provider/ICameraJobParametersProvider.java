package com.sicpa.standard.sasscl.devices.camera.jobconfig.parameters.provider;

import java.util.Collection;

import com.sicpa.standard.sasscl.devices.camera.jobconfig.parameters.type.ICameraJobParameter;

public interface ICameraJobParametersProvider {

	Collection<ICameraJobParameter> getParameterList();

}
