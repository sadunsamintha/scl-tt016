package com.sicpa.standard.sasscl.devices.camera;

import com.sicpa.standard.sasscl.model.Code;

public class CameraBadCodeEvent {

	protected Code code;
	protected ICameraAdaptor source;

	public CameraBadCodeEvent(final Code code, ICameraAdaptor source) {
		this.code = code;
		this.source = source;
	}

	public Code getCode() {
		return this.code;
	}

	public ICameraAdaptor getSource() {
		return source;
	}
}
