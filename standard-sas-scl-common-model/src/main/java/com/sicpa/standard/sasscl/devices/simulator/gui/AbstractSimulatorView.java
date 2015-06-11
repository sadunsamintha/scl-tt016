package com.sicpa.standard.sasscl.devices.simulator.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

public abstract class AbstractSimulatorView extends JPanel {

	private static final long serialVersionUID = -6663891623522770678L;
	protected JButton buttonConnect;
	protected JButton buttonDisconnect;

	public AbstractSimulatorView() {
	}

	protected void initGUI() {
		setLayout(new MigLayout());
		add(getButtonConnect());
		add(getButtonDisconnect());

	}

	public JButton getButtonConnect() {
		if (buttonConnect == null) {
			buttonConnect = new JButton("Connect");
			buttonConnect.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					buttonConnectActionPerformed();
				}
			});
		}
		return this.buttonConnect;
	}

	public JButton getButtonDisconnect() {
		if (buttonDisconnect == null) {
			buttonDisconnect = new JButton("Disconnect");
			buttonDisconnect.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					buttonDisconnectActionPerformed();
				}
			});
		}
		return buttonDisconnect;
	}

	protected abstract void buttonConnectActionPerformed();

	protected abstract void buttonDisconnectActionPerformed();

}
