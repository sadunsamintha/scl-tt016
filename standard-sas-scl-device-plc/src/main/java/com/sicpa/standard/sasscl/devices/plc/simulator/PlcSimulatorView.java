package com.sicpa.standard.sasscl.devices.plc.simulator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractSpinnerModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerNumberModel;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.gui.plaf.SicpaLookAndFeel;
import com.sicpa.standard.plc.controller.PlcException;
import com.sicpa.standard.plc.controller.internal.PlcNotificationImpl;
import com.sicpa.standard.plc.driver.event.PlcEventCode;
import com.sicpa.standard.plc.value.IPlcVariable;
import com.sicpa.standard.plc.value.PlcBoolean;
import com.sicpa.standard.plc.value.PlcInt32;
import com.sicpa.standard.sasscl.devices.simulator.gui.AbstractSimulatorView;

public class PlcSimulatorView extends AbstractSimulatorView {

	protected static final long serialVersionUID = 6312340007019994903L;

	@SuppressWarnings("unused")
	private static Logger logger = LoggerFactory.getLogger(PlcSimulatorView.class);

	protected PlcSimulatorController controller;

	// combo boxs
	protected JComboBox plcVariableComboBox;
	protected JComboBox plcNotificationVariableComboBox;

	// buttons
	protected JButton readButton;
	protected JButton writeButton;
	protected JButton sendButton;
	protected JButton connectButton;
	protected JButton disconnectButton;

	// spinners
	protected JSpinner variableValueSpinner;
	protected JSpinner notificationValueSpinner;

	// labels
	protected JLabel statusLabel;

	// panels
	protected JPanel connectDisconnectButtonsPanel;
	protected JPanel readWritePlcVariablePanel;
	protected JPanel sendNotificationPanel;

	protected static final String TRUE = Boolean.TRUE.toString().toUpperCase();
	protected static final String FALSE = Boolean.FALSE.toString().toUpperCase();

	public PlcSimulatorView(final PlcSimulatorController controller) {

		this.controller = controller;
		initGUI();
	}

	protected void setupUIComponent() {

		connectButton = getButtonConnect();
		disconnectButton = getButtonDisconnect();
		setupPlcVariableComboBox();
		setupButtonRead();
		setupWriteButton();
		setupSendButton();
		setupNotificationComboBox();
		setupStatusLabel();
		setupVariableValueSpinner();
		setupNotificationValueSpinner();
	}

	@Override
	protected void initGUI() {

		setLayout(new MigLayout());

		setupUIComponent();

		// setup connect / disconnect button panel
		connectDisconnectButtonsPanel = new JPanel();
		connectDisconnectButtonsPanel.setLayout(new MigLayout());
		connectDisconnectButtonsPanel.add(connectButton);
		connectDisconnectButtonsPanel.add(disconnectButton);
		add(connectDisconnectButtonsPanel, "newline");

		// setup read write plc variable panel
		readWritePlcVariablePanel = new JPanel();
		readWritePlcVariablePanel.setLayout(new MigLayout());
		readWritePlcVariablePanel.add(new JLabel("Read Write PLC Variable : "), "newline");
		readWritePlcVariablePanel.add(new JLabel("Variable Name"), "newline");
		readWritePlcVariablePanel.add(plcVariableComboBox, "grow");
		readWritePlcVariablePanel.add(readButton, "wrap");
		readWritePlcVariablePanel.add(new JLabel("Variable Value"), "newline");
		readWritePlcVariablePanel.add(variableValueSpinner, "grow");
		readWritePlcVariablePanel.add(writeButton, "wrap");
		add(readWritePlcVariablePanel, "newline");

		// setup send notificaiton panel
		sendNotificationPanel = new JPanel();
		sendNotificationPanel.setLayout(new MigLayout());
		sendNotificationPanel.add(new JLabel("Send Notification : "), "newline");
		sendNotificationPanel.add(plcNotificationVariableComboBox, "newline");
		sendNotificationPanel.add(notificationValueSpinner, "grow");
		sendNotificationPanel.add(sendButton, "wrap");
		add(sendNotificationPanel, "newline");

		// add status label
		add(statusLabel, "newline");

	}

	/**
	 * hide connect / disconnect panel and send notification panel
	 */
	public void hideProductionPanel() {

		connectDisconnectButtonsPanel.setVisible(false);
		sendNotificationPanel.setVisible(false);
	}

	@Override
	protected void buttonConnectActionPerformed() {

		controller.fireStatusChanged(PlcEventCode.CONNECTED);
	}

	@Override
	protected void buttonDisconnectActionPerformed() {

		controller.fireStatusChanged(PlcEventCode.DISCONNECTED);
	}

	protected void setupPlcVariableComboBox() {

		if (plcVariableComboBox == null) {
			plcVariableComboBox = new JComboBox();

			for (String s : controller.getPlcVariableMap().keySet()) {
				plcVariableComboBox.addItem(s);
			}

			ItemListener itemListener = new ItemListener() {

				@SuppressWarnings("rawtypes")
				public void itemStateChanged(ItemEvent itemEvent) {

					String variable = itemEvent.getItem().toString();

					// retrieve variable type from variable map
					IPlcVariable plcVariable = controller.getPlcVariableMap().get(variable);

					if (plcVariable.getValueType().equals(Boolean.class)) {
						variableValueSpinner.setModel(new SpinnerListModel(new String[] { TRUE, FALSE }));
						variableValueSpinner.setValue(FALSE);
					}
					if (plcVariable.getValueType().equals(Integer.class)) {
						variableValueSpinner.setModel(new SpinnerNumberModel(0, 0, 5000, 1));
						variableValueSpinner.setValue(0);
					}

					statusLabel.setText("");
				}
			};
			this.plcVariableComboBox.addItemListener(itemListener);

		}

	}

	@SuppressWarnings("rawtypes")
	public void setupNotificationComboBox() {

		if (plcNotificationVariableComboBox == null) {
			plcNotificationVariableComboBox = new JComboBox();

			// get only registered notification
			for (PlcNotificationImpl notification : controller.getRegisteredNotifications().values()) {
				plcNotificationVariableComboBox.addItem(notification.getVarName());
			}

			plcNotificationVariableComboBox.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					statusLabel.setText("");

					String variable = ((JComboBox) e.getSource()).getSelectedItem().toString();

					// retrieve variable type from variable map
					IPlcVariable plcVariable = controller.getPlcVariableMap().get(variable);

					if (plcVariable.getValueType().equals(Boolean.class)) {
						notificationValueSpinner.setModel(new SpinnerListModel(new String[] { TRUE, FALSE }));
						notificationValueSpinner.setValue(FALSE);
					}

					if (plcVariable.getValueType().equals(Integer.class)) {
						notificationValueSpinner.setModel(new SpinnerNumberModel(0, 0, 5000, 1));
						notificationValueSpinner.setValue(0);
					}

					if (plcVariable.getValueType().equals(String.class)) {

						notificationValueSpinner.setModel(new StringSpinnerModel());
						notificationValueSpinner.setValue(":");
					}
					setSpinnerEditable(notificationValueSpinner);
				}
			});

		}

	}

	public void setupButtonRead() {

		if (readButton == null) {
			readButton = new JButton("Read");
			readButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					buttonReadActionPerformed();
				}
			});
		}
	}

	protected void buttonReadActionPerformed() {

		String itemSelected = plcVariableComboBox.getSelectedItem().toString();
		IPlcVariable<?> variable = controller.getPlcVariableMap().get(itemSelected);
		Object value;

		try {
			if (variable != null) {

				value = controller.getPlcValue(variable);

				if (variable.getValueType().equals(Boolean.class)) {

					if (value instanceof String) {
						// convert string to boolean
						value = Boolean.valueOf(value.toString());
					}

					variableValueSpinner.setValue(value.toString().toUpperCase());
				} else if (variable.getValueType().equals(Integer.class)) {
					if (value instanceof String) {
						variableValueSpinner.setValue(Integer.valueOf(value.toString()));
					} else {
						variableValueSpinner.setValue(value);
					}
				}

			} else {
				statusLabel.setText("Variable not found.");
			}

		} catch (PlcException e) {
		}
	}

	public void setupWriteButton() {

		if (writeButton == null) {
			writeButton = new JButton("Write");
			writeButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					buttonWriteActionPerformed();
				}
			});
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void buttonWriteActionPerformed() {

		String itemSelected = plcVariableComboBox.getSelectedItem().toString();
		String value = variableValueSpinner.getValue().toString(); // textFieldValue.getText();

		IPlcVariable variable = controller.getPlcVariableMap().get(itemSelected);

		if (variable != null) {
			if (variable.getVariableType().equals(PlcBoolean.class)) {
				variable.setValue(value.equals(TRUE));
			} else if (variable.getVariableType().equals(PlcInt32.class)) {
				variable.setValue(Integer.parseInt(value));
			}

			statusLabel.setText("Value is written to PLC.");

		} else {
			statusLabel.setText("Write Failed. Variable not found.");
		}
	}

	public void setupSendButton() {

		if (sendButton == null) {
			sendButton = new JButton("Send");
			sendButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					buttonSendActionPerformed();
				}
			});
		}
	}

	protected void buttonSendActionPerformed() {

		String itemSelected = plcNotificationVariableComboBox.getSelectedItem().toString();
		Object value = toTypedValue(notificationValueSpinner.getValue().toString(),
				controller.getPlcVariableMap().get(itemSelected));

		controller.sendNotification(itemSelected, value);
		statusLabel.setText("Request " + itemSelected + " sent to PLC #" + value);
	}

	@SuppressWarnings("rawtypes")
	private Object toTypedValue(String strValue, IPlcVariable variable) {

		if (variable.getValueType().equals(Boolean.class)) {
			return Boolean.parseBoolean(strValue);
		}

		if (variable.getValueType().equals(Integer.class)) {
			return Integer.parseInt(strValue);
		}

		if (variable.getValueType().equals(String.class)) {
			return strValue;
		}
		return null;
	}

	public void setupStatusLabel() {

		if (statusLabel == null) {
			statusLabel = new JLabel();
		}
	}

	public void setupVariableValueSpinner() {

		if (variableValueSpinner == null) {
			SpinnerNumberModel model = new SpinnerNumberModel(0, 0, 5000, 1);
			variableValueSpinner = new JSpinner(model);
			setSpinnerEditable(variableValueSpinner);
		}
	}

	public void setupNotificationValueSpinner() {

		if (notificationValueSpinner == null) {
			SpinnerNumberModel model = new SpinnerNumberModel(0, 0, 5000, 1);
			notificationValueSpinner = new JSpinner(model);
			setSpinnerEditable(notificationValueSpinner);
		}
	}

	private void setSpinnerEditable(JSpinner spinner) {

		SicpaLookAndFeel.turnOffVirtualKeyboardOnSpinner(spinner);
		JComponent editor = spinner.getEditor();
		if ((editor != null) && (editor instanceof JSpinner.DefaultEditor)) {
			final JTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
			if (tf != null) {
				tf.setEditable(true);
				tf.setOpaque(true);
			}
		}
	}

	private static class StringSpinnerModel extends AbstractSpinnerModel {

		private String inputString = StringUtils.EMPTY;

		@Override
		public Object getValue() {
			return this.inputString;
		}

		@Override
		public void setValue(Object value) {
			this.inputString = value.toString();
		}

		@Override
		public Object getNextValue() {
			return this.inputString;
		}

		@Override
		public Object getPreviousValue() {
			return this.inputString;
		}

	}

	// private static class StringSpinnerModel extends AbstractSpinnerModel {
	//
	// private String sku = StringUtils.EMPTY;
	// private int count;
	//
	// @Override
	// public Object getValue() {
	//
	// logger.debug("returning " + sku + ":" + count);
	// return sku + ":" + count;
	// }
	//
	// @Override
	// public void setValue(Object value) {
	//
	// String s = value.toString();
	// logger.debug("setting value " + s);
	// if (s.length() == 1)
	// return;
	//
	// sku = s.charAt(0) == '0' ? StringUtils.EMPTY : s.substring(0, s.indexOf(":"));
	// count = s.endsWith(":") ? 0 : Integer.parseInt(s.substring(s.indexOf(":") + 1));
	// fireStateChanged();
	// }
	//
	// @Override
	// public Object getNextValue() {
	//
	// logger.debug("next " + sku + ":" + (count + 1));
	// return sku + ":" + (++count);
	// }
	//
	// @Override
	// public Object getPreviousValue() {
	//
	// logger.debug("previous " + sku + ":" + (count - 1));
	// return sku + ":" + (--count);
	// }
	// }
}
