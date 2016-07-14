package com.sicpa.standard.sasscl.devices.camera.jobconfig.parameters.type;

import com.sicpa.standard.sasscl.devices.camera.jobconfig.parameters.IJobParameterWriter;

public interface ICameraJobParameter {

	void writeValue(IJobParameterWriter writer);
}
