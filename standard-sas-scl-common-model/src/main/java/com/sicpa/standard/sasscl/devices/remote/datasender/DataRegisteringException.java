package com.sicpa.standard.sasscl.devices.remote.datasender;

@SuppressWarnings("serial")
public class DataRegisteringException extends Exception {

	public DataRegisteringException() {
		super();
	}

	public DataRegisteringException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public DataRegisteringException(String message, Throwable cause) {
		super(message, cause);
	}

	public DataRegisteringException(String message) {
		super(message);
	}

	public DataRegisteringException(Throwable cause) {
		super(cause);
	}

}
