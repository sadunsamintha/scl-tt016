package com.sicpa.standard.sasscl.common.log;

public class OperatorLogger {

	protected static ILoggerBehavior loggerBehavior = null;

	public static void setLoggerBehavior(ILoggerBehavior loggerBehavior) {
		OperatorLogger.loggerBehavior = loggerBehavior;
	}

	public static void log(String msg, Object... parameters) {
		try {
			loggerBehavior.log(msg, parameters);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println();
		}
	}

}
