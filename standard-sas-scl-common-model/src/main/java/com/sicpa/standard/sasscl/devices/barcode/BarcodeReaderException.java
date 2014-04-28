package com.sicpa.standard.sasscl.devices.barcode;

import com.sicpa.standard.sasscl.devices.DeviceException;

/**
 * This exception is thrown when an exception from the barcode reader device is caught. E.g.: barcode reader connection related event.
 * 
 *
 */
public class BarcodeReaderException extends DeviceException {

	private static final long serialVersionUID = -313819964547678668L;

	public BarcodeReaderException() {
		super();
	}

	public BarcodeReaderException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public BarcodeReaderException(final String message) {
		super(message);
	}

	public BarcodeReaderException(final Throwable cause, final String msgKey, final Object... msgParam) {
		super(cause, msgKey, msgParam);
	}

	public BarcodeReaderException(final Throwable cause) {
		super(cause);
	}
}
