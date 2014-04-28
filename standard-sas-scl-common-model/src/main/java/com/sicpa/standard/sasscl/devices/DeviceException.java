package com.sicpa.standard.sasscl.devices;

import java.text.MessageFormat;

/**
 * base exception class for all device exceptions
 * 
 * @author DIelsch
 * 
 */
public class DeviceException extends Exception {

	private static final long serialVersionUID = 1L;

	public DeviceException() {
	}

	public DeviceException(final String message) {
		super(message);
	}

	public DeviceException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public DeviceException(final Throwable cause) {
		super(cause);
	}

	public DeviceException(final Throwable cause, final String msgKey, final Object... msgParam) {
		super(MessageFormat.format(msgKey, msgParam), cause);
	}
}
