package com.sicpa.standard.sasscl.devices.d900;

import com.sicpa.standard.sasscl.model.Code;

public abstract class D900CameraCodeEvent {

	private String code;

	public D900CameraCodeEvent(String code) {
		this.code = code;
	}

	public String getCode() {
		return this.code;
	}
}
