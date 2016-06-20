package com.sicpa.standard.sasscl.devices.bis.simulator;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.sicpa.standard.sasscl.devices.simulator.gui.AbstractSimulatorView;

@SuppressWarnings("serial")
public class BisSimulatorView extends AbstractSimulatorView {

	private BisControllerSimulator bis;
	private JTextField textSkuIdRecognized;
	private JButton buttonSendSkuRecognized;

	public BisSimulatorView(BisControllerSimulator bis) {
		this.bis = bis;
		initGUI();
	}

	@Override
	protected void initGUI() {
		super.initGUI();
		add(new JLabel("SKU ID:"), "newline");
		add(getTextSkuIdRecognized(), "growx , w 100");
		add(getButtonSendSkuRecognized());
	}

	@Override
	protected void buttonConnectActionPerformed() {
		bis.onConnected();
	}

	@Override
	protected void buttonDisconnectActionPerformed() {
		bis.onDisconnected();
	}

	public JButton getButtonSendSkuRecognized() {
		if (buttonSendSkuRecognized == null) {
			buttonSendSkuRecognized = new JButton("send");
			buttonSendSkuRecognized.addActionListener(e -> buttonSendSkuRecognizedActionPerformed());
		}
		return buttonSendSkuRecognized;
	}

	private void buttonSendSkuRecognizedActionPerformed() {
		bis.skuRecognized(Integer.parseInt(getTextSkuIdRecognized().getText()));
	}

	public JTextField getTextSkuIdRecognized() {
		if (textSkuIdRecognized == null) {
			textSkuIdRecognized = new JTextField();
			textSkuIdRecognized.addActionListener(e -> buttonSendSkuRecognizedActionPerformed());
		}
		return textSkuIdRecognized;
	}
}
