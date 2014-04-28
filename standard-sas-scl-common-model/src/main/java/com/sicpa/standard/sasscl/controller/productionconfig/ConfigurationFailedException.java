package com.sicpa.standard.sasscl.controller.productionconfig;

public class ConfigurationFailedException extends Exception {

	private static final long serialVersionUID = 1L;

	protected boolean recoverable = true;

	public ConfigurationFailedException() {
		super();
	}

	public ConfigurationFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConfigurationFailedException(String message) {
		super(message);
	}

	public ConfigurationFailedException(Throwable cause) {
		super(cause);
	}

	public boolean isRecoverable() {
		return recoverable;
	}

	public void setRecoverable(boolean recoverable) {
		this.recoverable = recoverable;
	}

}
