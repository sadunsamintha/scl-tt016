package com.sicpa.standard.sasscl.event;

public class ApplicationVersionEvent {

	protected String version;

	public ApplicationVersionEvent(String version) {
		this.version = version;
	}

	public String getVersion() {
		return version;
	}

}
