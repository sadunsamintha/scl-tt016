package com.sicpa.standard.sasscl.devices.camera;

import com.sicpa.standard.sasscl.model.Code;

public class CameraGoodCodeEvent extends CameraCodeEvent {

	public CameraGoodCodeEvent(Code code, ICameraAdaptor source) {
		super(code, source);
	}

	@Override
	public String toString() {
		return "CameraGoodCodeEvent{" + "code=" + getCode() + '}';
	}
}
