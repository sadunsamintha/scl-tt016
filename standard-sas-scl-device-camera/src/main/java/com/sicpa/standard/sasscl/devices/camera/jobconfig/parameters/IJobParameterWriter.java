package com.sicpa.standard.sasscl.devices.camera.jobconfig.parameters;

public interface IJobParameterWriter {
	
	void  writeIntValue(String name, Integer value);
	
	void  writeStringValue(String name, String value);
	
	void  writeAliasValue(String name, String value);
}
