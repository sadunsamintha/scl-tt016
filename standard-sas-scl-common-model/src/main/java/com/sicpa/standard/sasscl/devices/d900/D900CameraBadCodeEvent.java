package com.sicpa.standard.sasscl.devices.d900;

import com.sicpa.standard.sasscl.model.Code;

public class D900CameraBadCodeEvent extends D900CameraCodeEvent {

	public D900CameraBadCodeEvent(String code) {
		super(code);
	}

	@Override
	public String toString() {
		return "D900BadCodeEvent{" + "code=" + getCode() + '}';
	}

}
