package com.sicpa.standard.sasscl.devices.camera;

import com.sicpa.standard.sasscl.model.Code;

public abstract class CameraCodeEvent {

	private Code code;
	private ICameraAdaptor source;

	public CameraCodeEvent(Code code, ICameraAdaptor source) {
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
