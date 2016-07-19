package com.sicpa.standard.sasscl.devices.camera;

import com.sicpa.standard.sasscl.model.Code;

public class CameraGoodCodeEvent extends CameraCodeEvent {

	public CameraGoodCodeEvent(Code code) {
		super(code);
	}

	@Override
	public String toString() {
		return "CameraGoodCodeEvent{" + "code=" + getCode() + '}';
	}
}
