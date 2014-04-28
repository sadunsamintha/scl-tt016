package com.sicpa.standard.sasscl.devices.remote.simulator;

import com.sicpa.standard.sasscl.devices.simulator.gui.AbstractSimulatorView;

public class RemoteServerSimulatorView extends AbstractSimulatorView {

	private static final long serialVersionUID = 7974596346698734352L;

	protected RemoteServerSimulator controller;

	public RemoteServerSimulatorView(RemoteServerSimulator controller) {
		this.controller = controller;
		initGUI();
	}

	protected void initGUI() {
		super.initGUI();
	}

	protected void buttonConnectActionPerformed() {
		this.controller.doConnect();
	}

	protected void buttonDisconnectActionPerformed() {
		this.controller.doDisconnect();
	}
}
