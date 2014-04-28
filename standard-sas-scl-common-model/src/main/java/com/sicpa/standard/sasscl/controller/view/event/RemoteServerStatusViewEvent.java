package com.sicpa.standard.sasscl.controller.view.event;

public class RemoteServerStatusViewEvent {

	protected boolean connected;

	public RemoteServerStatusViewEvent(final boolean connected) {
		this.connected = connected;
	}

	public boolean isConnected() {
		return this.connected;
	}
}
