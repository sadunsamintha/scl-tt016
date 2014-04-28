package com.sicpa.standard.sasscl.sicpadata;

import java.text.MessageFormat;

/**
 * 
 * This exception is thrown when cryptography exceptions occur e.g. encoder failed to provide codes, authenticator
 * failed to decode an encrypted code.
 * 
 * @author YYang
 * 
 * 
 */
public class CryptographyException extends Exception {

	private static final long serialVersionUID = -6887536822496724839L;
	public CryptographyException() {
	}

	public CryptographyException(final String message) {
		super(message);
	}

	public CryptographyException(final Throwable cause) {
		super(cause);
	}

	public CryptographyException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public CryptographyException(final Throwable cause, final String msgKey, final Object... msgParam) {
		super(MessageFormat.format(msgKey, msgParam), cause);
	}

}
