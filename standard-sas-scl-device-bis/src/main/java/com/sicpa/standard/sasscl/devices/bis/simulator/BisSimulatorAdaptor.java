package com.sicpa.standard.sasscl.devices.bis.simulator;

import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.sasscl.devices.bis.BisAdapter;
import com.sicpa.standard.sasscl.devices.bis.BisAdaptorException;
import com.sicpa.standard.sasscl.devices.simulator.gui.SimulatorControlView;

public class BisSimulatorAdaptor extends BisAdapter {

	private SimulatorControlView simulatorGui;

	public BisSimulatorAdaptor() {
	}

	@Override
	protected void doConnect() throws BisAdaptorException {
		super.doConnect();
		showGui();
	}

	@Override
	protected void doDisconnect() throws BisAdaptorException {
		super.doDisconnect();
		hideGui();
	}

	private void showGui() {
		if (simulatorGui != null) {
			ThreadUtils.invokeLater(() -> simulatorGui.addSimulator(getName(), new BisSimulatorView(
					(BisControllerSimulator) controller)));
		}
	}

	private void hideGui() {
		if (simulatorGui != null) {
			ThreadUtils.invokeLater(() -> simulatorGui.removeSimulator(getName()));
		}
	}

	public void setSimulatorGui(SimulatorControlView simulatorGui) {
		this.simulatorGui = simulatorGui;
	}
}
