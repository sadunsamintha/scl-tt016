package com.sicpa.standard.sasscl.common.exception;

import com.sicpa.standard.client.common.exception.ClientRuntimeException;


/**
 * Exception that should be thrown if something goes wrong during the production that is not recoverable, meaning the
 * production has to be stopped
 * 
 * @author DIelsch
 * 
 */
public class ProductionRuntimeException extends ClientRuntimeException {

	private static final long serialVersionUID = 1L;

	public ProductionRuntimeException(String messageKey, Object... params) {
		super(messageKey, params);
	}

	public ProductionRuntimeException(String messageKey, Throwable cause, Object... params) {
		super(messageKey, cause, params);
	}

	public ProductionRuntimeException(String messageKey) {
		super(messageKey);
	}

	public ProductionRuntimeException(Throwable cause) {
		super(cause);
	}

}
