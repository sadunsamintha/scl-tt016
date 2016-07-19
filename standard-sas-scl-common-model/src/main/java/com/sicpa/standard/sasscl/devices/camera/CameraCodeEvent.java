package com.sicpa.standard.sasscl.devices.camera;

import com.sicpa.standard.sasscl.model.Code;

public abstract class CameraCodeEvent {

	private Code code;

	public CameraCodeEvent(Code code) {
		this.code = code;
	}

	public Code getCode() {
		return this.code;
	}
}
