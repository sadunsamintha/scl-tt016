package com.sicpa.standard.sasscl.controller.hardware;

import java.util.Collection;


public interface IDeviceErrorRepository {

	/**
	 * 
	 * @param deviceId
	 * @param errorCode
	 * @return true if the error was not yet present in the repository
	 */
	 boolean addError(String deviceId, String errorCode,String errorToDisplay);

	 void removeError(String deviceId, String errorCode);
	 
	 void removeAllErrors(String deviceId);
	
	 Collection<String> getErrors();
	 
	 Collection<String> getErrors(String deviceName);
	 

	 boolean isEmpty();
	 
	 void reset();
	
	
}
