package com.sicpa.standard.sasscl.devices.camera;

import com.sicpa.standard.sasscl.model.Code;

public class CameraBadCodeEvent extends CameraCodeEvent {

	public CameraBadCodeEvent(Code code) {
		super(code);
	}

	@Override
	public String toString() {
		return "CameraBadCodeEvent{" + "code=" + getCode() + '}';
	}

}
