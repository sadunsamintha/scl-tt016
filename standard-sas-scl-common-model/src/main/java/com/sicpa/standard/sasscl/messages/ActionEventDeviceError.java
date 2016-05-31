package com.sicpa.standard.sasscl.messages;


public class ActionEventDeviceError extends ActionEvent {

	public ActionEventDeviceError(String key, Object source, Object... param) {
		super(key, source, param);
	}

	public ActionEventDeviceError() {
	}
	
	@Override
	public String getKey() {
		// TODO Auto-generated method stub
		return super.getKey();
	}
}
