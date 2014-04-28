package com.sicpa.standard.sasscl.devices.camera;

import com.sicpa.standard.sasscl.devices.DeviceException;


public class CameraAdaptorException extends DeviceException {

	private static final long serialVersionUID = 1L;

	public CameraAdaptorException() {
		super();
	}

	public CameraAdaptorException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public CameraAdaptorException(final String message) {
		super(message);
	}

	public CameraAdaptorException(final Throwable cause, final String msgKey, final Object... msgParam) {
		super(cause, msgKey, msgParam);
	}

	public CameraAdaptorException(final Throwable cause) {
		super(cause);
	}
}
