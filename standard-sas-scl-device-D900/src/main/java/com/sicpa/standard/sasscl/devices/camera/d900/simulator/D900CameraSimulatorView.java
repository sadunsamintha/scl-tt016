package com.sicpa.standard.sasscl.devices.camera.d900.simulator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.sicpa.standard.camera.d900.driver.D900CameraDriverEventCode;
import com.sicpa.standard.sasscl.devices.simulator.gui.AbstractSimulatorView;

@SuppressWarnings("serial")
public class D900CameraSimulatorView extends AbstractSimulatorView {

	protected D900CameraSimulatorController controller;
	protected JButton buttonBadCode;
	protected JButton buttonSendCode;
	protected JTextField textCode;
	protected JSpinner spinnerReadCodeInterval;
	protected JSpinner spinnerBadCodePercentage;

	public D900CameraSimulatorView(D900CameraSimulatorController controller) {
		this.controller = controller;
		initGUI();
	}

	protected void initGUI() {
		super.initGUI();
		add(getButtonBadCode(), "newline");
		add(getTextCode(), "grow");
		add(getButtonSendCode(), "wrap");
		add(new JLabel("read code interval(ms):"), "spanx , split 2");
		add(getSpinnerReadCodeInterval(), "grow");
		add(new JLabel("Bad code Percentage"), "spanx , split 2");
		add(getSpinnerBadCodePercentage(), "grow");
	}

	protected void buttonConnectActionPerformed() {
		controller.fireStatusChanged(D900CameraDriverEventCode.CONNECTED);
	}

	protected void buttonDisconnectActionPerformed() {
		controller.stopReading();
		controller.fireStatusChanged(D900CameraDriverEventCode.DISCONNECTED);
	}

	public JButton getButtonBadCode() {
		if (buttonBadCode == null) {
			buttonBadCode = new JButton("Send a bad code");
			buttonBadCode.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					buttonBadCodeActionPerformed();
				}
			});
		}
		return buttonBadCode;
	}

	protected void buttonBadCodeActionPerformed() {
		controller.fireBadCode("");
	}

	public JButton getButtonSendCode() {
		if (buttonSendCode == null) {
			buttonSendCode = new JButton("Send code");
			buttonSendCode.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					buttonSendCodeActionPerformed();
				}
			});
		}
		return buttonSendCode;
	}

	protected void buttonSendCodeActionPerformed() {
		controller.fireGoodCode(getTextCode().getText());
	}

	public JTextField getTextCode() {
		if (textCode == null) {
			textCode = new JTextField(15);
		}
		return textCode;
	}

	public JSpinner getSpinnerReadCodeInterval() {
		if (spinnerReadCodeInterval == null) {
			SpinnerNumberModel model = new SpinnerNumberModel(controller.getCameraModel().getReadCodeInterval(), 0,
					5000, 1);
			
			if (controller.getCameraModel().getReadCodeInterval() < 0) {
				// it happens in functional tests
				model.setValue(0);
			}

			spinnerReadCodeInterval = new JSpinner(model);
			spinnerReadCodeInterval.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					spinnerReadCodeIntervalValueChanged();
				}
			});
		}
		return spinnerReadCodeInterval;
	}

	protected void spinnerReadCodeIntervalValueChanged() {
		controller.getCameraModel().setReadCodeInterval((Integer) getSpinnerReadCodeInterval().getValue());
	}

	public JSpinner getSpinnerBadCodePercentage() {
		if (spinnerBadCodePercentage == null) {
			SpinnerNumberModel model = new SpinnerNumberModel(controller.getCameraModel().getPercentageBadCode(), 0,
					100, 1);
			spinnerBadCodePercentage = new JSpinner(model);
			spinnerBadCodePercentage.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					spinnerBadCodePercentageChanged();
				}
			});
		}
		return spinnerBadCodePercentage;
	}

	protected void spinnerBadCodePercentageChanged() {
		controller.getCameraModel().setPercentageBadCode((Integer) getSpinnerBadCodePercentage().getValue());
	}
}
