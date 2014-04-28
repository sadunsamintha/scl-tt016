package com.sicpa.standard.sasscl.devices.printer;

import java.text.MessageFormat;

import com.sicpa.standard.sasscl.devices.DeviceException;

/**
 * This exception is thrown when an exception from the printer device is caught. E.g.: printer connection related event.
 * 
 * @author GTeo
 * 
 */
public class PrinterAdaptorException extends DeviceException {

	private static final long serialVersionUID = 1L;

	public PrinterAdaptorException() {
		super();
	}

	public PrinterAdaptorException(final String message) {
		super(message);
	}

	public PrinterAdaptorException(final Throwable cause) {
		super(cause);
	}

	public PrinterAdaptorException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public PrinterAdaptorException(final Throwable cause, final String msgKey, final Object... msgParam) {
		super(MessageFormat.format(msgKey, msgParam), cause);
	}

}
