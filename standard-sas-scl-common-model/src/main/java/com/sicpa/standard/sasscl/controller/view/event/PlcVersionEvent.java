package com.sicpa.standard.sasscl.controller.view.event;

public class PlcVersionEvent {

	protected String plcVersion;

	public PlcVersionEvent() {
	}

	public PlcVersionEvent(String plcVersion) {
		this.plcVersion = plcVersion;
	}

	public String getPlcVersion() {
		return plcVersion;
	}

	public void setPlcVersion(String plcVersion) {
		this.plcVersion = plcVersion;
	}
}
