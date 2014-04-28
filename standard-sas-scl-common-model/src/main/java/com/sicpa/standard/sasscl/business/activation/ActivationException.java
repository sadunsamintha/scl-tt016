package com.sicpa.standard.sasscl.business.activation;

/**
 * General exception to be thrown on activation failures
 * 
 */
public class ActivationException extends Exception {

	private static final long serialVersionUID = 1L;

	public ActivationException() {
		super();
	}

	public ActivationException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public ActivationException(final String message) {
		super(message);
	}

	public ActivationException(final Throwable cause) {
		super(cause);
	}
}
