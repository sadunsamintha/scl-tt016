package com.sicpa.standard.sasscl.common.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OperatorLoggerBehavior implements ILoggerBehavior {

	private static final Logger logger = LoggerFactory.getLogger(OperatorLoggerBehavior.class);

	/**
	 * Add operator action log as info level message
	 * 
	 * @param msg
	 * @param parameters
	 */
	@Override
	public void log(String msg, Object... parameters) {
		logger.info(msg, parameters);

	}
}
