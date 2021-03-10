package com.sicpa.standard.sasscl.devices.d900;

import com.sicpa.standard.sasscl.devices.DeviceException;


public class D900CameraAdaptorException extends DeviceException {

	private static final long serialVersionUID = 1L;

	public D900CameraAdaptorException() {
		super();
	}

	public D900CameraAdaptorException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public D900CameraAdaptorException(final String message) {
		super(message);
	}

	public D900CameraAdaptorException(final Throwable cause, final String msgKey, final Object... msgParam) {
		super(cause, msgKey, msgParam);
	}

	public D900CameraAdaptorException(final Throwable cause) {
		super(cause);
	}
}
