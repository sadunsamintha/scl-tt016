package com.sicpa.standard.sasscl.devices.remote.simulator;

import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.sicpadata.generator.IEncoder;

public interface ISimulatorGetEncoder {
	IEncoder getEncoder(CodeType codeType);
}