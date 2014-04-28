package com.sicpa.standard.sasscl.devices.barcode.simulator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JTextField;

import com.sicpa.standard.sasscl.devices.simulator.gui.AbstractSimulatorView;

public class BarcodeReaderSimulatorView extends AbstractSimulatorView {

	private static final long serialVersionUID = 1L;

	protected BarcodeReaderSimulator controller;

	protected JTextField textBarcode;
	protected JButton buttonSend;

	public BarcodeReaderSimulatorView(BarcodeReaderSimulator controller) {
		this.controller = controller;
		initGUI();
	}

	protected void initGUI() {
		super.initGUI();
		add(getTextBarcode(), "newline,grow");
		add(getButtonSend(), "wrap");
	}

	protected void buttonConnectActionPerformed() {
		controller.notifyOnBarcodeConnection(true);
	}

	protected void buttonDisconnectActionPerformed() {
		controller.notifyOnBarcodeConnection(false);
	}

	public JTextField getTextBarcode() {
		if (this.textBarcode == null) {
			this.textBarcode = new JTextField(15);
			this.textBarcode.setText("001");
			this.textBarcode.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					buttonSendActionPerformed();
				}
			});
		}
		return this.textBarcode;
	}

	protected void buttonSendActionPerformed() {
		controller.notifyBarcodeRead(getTextBarcode().getText());
	}

	public JButton getButtonSend() {
		if (this.buttonSend == null) {
			this.buttonSend = new JButton("Send");
			buttonSend.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					buttonSendActionPerformed();
				}
			});
		}
		return this.buttonSend;
	}
}
