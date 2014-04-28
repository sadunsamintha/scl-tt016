package com.sicpa.standard.sasscl.common.log;

public interface ILoggerBehavior {
	
	/**
	 * Add log for actions
	 * 
	 * @param msg
	 * @param parameters
	 */
	void log(String msg, Object... parameters);


}
