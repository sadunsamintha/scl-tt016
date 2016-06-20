package com.sicpa.standard.sasscl.devices.bis.simulator;

import javax.swing.JButton;
import javax.swing.JTextField;

import com.sicpa.standard.sasscl.devices.simulator.gui.AbstractSimulatorView;

@SuppressWarnings("serial")
public class BisSimulatorView extends AbstractSimulatorView {

	private BisSimulatorAdaptor bis;
	private JTextField textSkuIdRecognized;
	private JButton buttonSendSkuRecognized;

	public BisSimulatorView(BisSimulatorAdaptor bis) {
		this.bis = bis;
		initGUI();
	}

	@Override
	protected void initGUI() {
		super.initGUI();
		add(getTextSkuIdRecognized(), "newline, growx , w 100");
		add(getButtonSendSkuRecognized());
	}

	@Override
	protected void buttonConnectActionPerformed() {
		bis.onConnection();
	}

	@Override
	protected void buttonDisconnectActionPerformed() {
		bis.onDisconnection();
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
