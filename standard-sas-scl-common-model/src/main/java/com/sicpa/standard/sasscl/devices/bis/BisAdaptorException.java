package com.sicpa.standard.sasscl.devices.bis;

import com.sicpa.standard.sasscl.devices.DeviceException;

public class BisAdaptorException extends DeviceException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3358769457359390775L;
	
	public BisAdaptorException() {
		super();
	}

	public BisAdaptorException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public BisAdaptorException(final String message) {
		super(message);
	}

	public BisAdaptorException(final Throwable cause, final String msgKey, final Object... msgParam) {
		super(cause, msgKey, msgParam);
	}

	public BisAdaptorException(final Throwable cause) {
		super(cause);
	}

}
