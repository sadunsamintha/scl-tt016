package com.sicpa.standard.sasscl.devices.plc;

import com.sicpa.standard.sasscl.devices.DeviceException;

public class PlcAdaptorException extends DeviceException {

	private static final long serialVersionUID = 1L;

	public PlcAdaptorException() {
		super();
	}

	public PlcAdaptorException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public PlcAdaptorException(final String message) {
		super(message);
	}

	public PlcAdaptorException(final Throwable cause, final String msgKey, final Object... msgParam) {
		super(cause, msgKey, msgParam);
	}

	public PlcAdaptorException(final Throwable cause) {
		super(cause);
	}

}
