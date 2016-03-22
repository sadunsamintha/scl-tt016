package com.sicpa.standard.sasscl.devices.plc;


public interface IPlcParamSender {

	void sendToPlc(String varName, String value,int lineIndex) throws PlcAdaptorException;

}
