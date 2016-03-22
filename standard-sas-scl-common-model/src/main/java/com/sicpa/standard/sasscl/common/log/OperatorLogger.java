package com.sicpa.standard.sasscl.common.log;

public class OperatorLogger {

	protected static ILoggerBehavior loggerBehavior = null;

	public static void setLoggerBehavior(ILoggerBehavior loggerBehavior) {
		OperatorLogger.loggerBehavior = loggerBehavior;
	}

	public static void log(String msg, Object... parameters) {
		loggerBehavior.log(msg, parameters);
	}

}
