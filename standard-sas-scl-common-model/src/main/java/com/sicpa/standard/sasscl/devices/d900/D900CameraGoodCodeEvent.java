package com.sicpa.standard.sasscl.devices.d900;

import com.sicpa.standard.sasscl.model.Code;

public class D900CameraGoodCodeEvent extends D900CameraCodeEvent {

	public D900CameraGoodCodeEvent(String code) {
		super(code);
	}

	@Override
	public String toString() {
		return "D900CameraGoodCodeEvent{" + "code=" + getCode() + '}';
	}
}
