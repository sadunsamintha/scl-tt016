package com.sicpa.standard.sasscl.devices.printer.simulator;

import com.sicpa.standard.printer.controller.model.FaultStatus;
import com.sicpa.standard.printer.controller.model.PrinterBufferStatus;
import com.sicpa.standard.printer.controller.model.SequenceStatus;
import com.sicpa.standard.printer.controller.model.command.PrinterMessage;
import com.sicpa.standard.printer.controller.model.command.PrinterMessageId;
import com.sicpa.standard.printer.driver.event.PrinterBufferStatusChangedEventArgs;
import com.sicpa.standard.sasscl.devices.simulator.gui.AbstractSimulatorView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

public class PrinterSimulatorView extends AbstractSimulatorView {

	private static final long serialVersionUID = 1L;

	protected PrinterSimulator simulator;
	protected JCheckBox jcbChargeFault;
	protected JCheckBox jcbGutter;
	protected JCheckBox jcbHighVoltage;
	protected JCheckBox jcbLowMakeUp;
	protected JCheckBox jcbReservoirLevelTooLow;
	protected JCheckBox jcbInkCartridgeLow;
	protected JButton buttonSendFault;
	protected JButton buttonNeedCodes;
	protected JTextArea textAllCodes;

	protected JCheckBox jcbStatusSeqinON;
	protected JCheckBox jcbStatusSeqinOFF;
	protected JCheckBox jcbStatusSeqOFF;
	protected JCheckBox jcbStatusSeqREADY;
	protected JButton buttonSetStatus;

	public PrinterSimulatorView(final PrinterSimulator simulator) {
		this.simulator = simulator;
		initGUI();
	}

	protected Timer timerUpdateCodeList = new Timer(5000, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			getTextAllCodes().setText("");
			for (String c : simulator.getCodesReadyToPrint()) {
				getTextAllCodes().append(c + "\n");
			}
		}
	});

	public void startTimerUpdateCodeList() {
		timerUpdateCodeList.start();
	}

	public void stopTimerUpdateCodeList() {
		timerUpdateCodeList.stop();
	}

	@Override
	protected void initGUI() {
		super.initGUI();
		add(getJcbChargeFault(), "newline , spanx , split 3");
		add(getJcbGutter());
		add(getJcbHighVoltage(), "wrap");
		add(getJcbInkCartridgeLow(), "newline , spanx , split 3");
		add(getJcbLowMakeUp());
		add(getJcbReservoirLevelTooLow());
		add(getButtonSendFault());
		add(getButtonNeedCodes(), "wrap");

		add(getJcbStatusSeqOFF());
		add(getJcbStatusSeqinON());
		add(getJcbStatusSeqinOFF());
		add(getJcbStatusSeqREADY(), "wrap");
		add(getButtonSetStatus(), "wrap");

		add(new JScrollPane(getTextAllCodes()), "grow,pushy, spanx");
	}

	@Override
	protected void buttonConnectActionPerformed() {
		this.simulator.startServer();
	}

	@Override
	protected void buttonDisconnectActionPerformed() {
		this.simulator.stopServer();
	}

	public JCheckBox getJcbGutter() {
		if (this.jcbGutter == null) {
			this.jcbGutter = new JCheckBox("Gutter");
			this.jcbGutter.setSelected(true);
		}
		return this.jcbGutter;
	}

	public JCheckBox getJcbHighVoltage() {
		if (this.jcbHighVoltage == null) {
			this.jcbHighVoltage = new JCheckBox("High voltage");
		}
		return this.jcbHighVoltage;
	}

	public JCheckBox getJcbInkCartridgeLow() {
		if (this.jcbInkCartridgeLow == null) {
			this.jcbInkCartridgeLow = new JCheckBox("Ink cartridge low");
		}
		return this.jcbInkCartridgeLow;
	}

	public JCheckBox getJcbLowMakeUp() {
		if (this.jcbLowMakeUp == null) {
			this.jcbLowMakeUp = new JCheckBox("Low make up");
		}
		return this.jcbLowMakeUp;
	}

	public JCheckBox getJcbReservoirLevelTooLow() {
		if (this.jcbReservoirLevelTooLow == null) {
			this.jcbReservoirLevelTooLow = new JCheckBox("reservoir level too low");
		}
		return this.jcbReservoirLevelTooLow;
	}

	public JCheckBox getJcbChargeFault() {
		if (this.jcbChargeFault == null) {
			this.jcbChargeFault = new JCheckBox("Charge");
		}
		return this.jcbChargeFault;
	}

	public JButton getButtonSendFault() {
		if (this.buttonSendFault == null) {
			this.buttonSendFault = new JButton("Send fault");
			this.buttonSendFault.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					buttonSendFaultActionPerformed();
				}
			});
		}
		return this.buttonSendFault;
	}

	public class PrinterFaultStatus extends FaultStatus {

		boolean gutterFault;
		boolean highVoltageFault;
		boolean makeUpCartridgeLow;
		boolean inkCartridgeLow;
		boolean chargeFault;
		boolean reservoirLevelTooLow;

		public PrinterFaultStatus() {
			super(0);
			gutterFault = getJcbGutter().isSelected();
			highVoltageFault = getJcbHighVoltage().isSelected();
			makeUpCartridgeLow = getJcbLowMakeUp().isSelected();
			chargeFault = getJcbChargeFault().isSelected();
			reservoirLevelTooLow = getJcbReservoirLevelTooLow().isSelected();
			inkCartridgeLow = getJcbInkCartridgeLow().isSelected();
			System.out.println(this);
		}

		@Override
		public String toString() {
			return "PrinterFaultStatus [gutterFault=" + gutterFault + ", highVoltageFault=" + highVoltageFault
					+ ", makeUpCartridgeLow=" + makeUpCartridgeLow + ", inkCartridgeLow=" + inkCartridgeLow
					+ ", chargeFault=" + chargeFault + ", reservoirLevelTooLow=" + reservoirLevelTooLow + "]";
		}

		public boolean isGutterFault() {
			return gutterFault;
		}

		public boolean isHighVoltageFault() {
			return highVoltageFault;
		}

		public boolean isMakeUpCartridgeLow() {
			return makeUpCartridgeLow;
		}

		public boolean isInkCartridgeLow() {
			return inkCartridgeLow;
		}

		public boolean isChargeFault() {
			return chargeFault;
		}

		public boolean isReservoirLevelTooLow() {
			return reservoirLevelTooLow;
		}

	}

	protected void buttonSendFaultActionPerformed() {
		this.simulator.messageReceived(new PrinterMessage(PrinterMessageId.CHARGE_FAULT));
	}

	public JButton getButtonNeedCodes() {
		if (this.buttonNeedCodes == null) {
			this.buttonNeedCodes = new JButton("Need codes");
			this.buttonNeedCodes.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					buttonNeedCodesActionPerformed();
				}
			});
		}
		return this.buttonNeedCodes;
	}

	protected void buttonNeedCodesActionPerformed() {
		PrinterBufferStatusChangedEventArgs evt = new PrinterBufferStatusChangedEventArgs();
		evt.setCodeNeeded(200);
		evt.setNeedCodes(true);
		evt.setBufferStatus(PrinterBufferStatus.UNKNOWN);
		this.simulator.onPrinterCodesNeeded(evt.getCodeNeeded());
	}

	public JTextArea getTextAllCodes() {
		if (this.textAllCodes == null) {
			this.textAllCodes = new JTextArea();
			this.textAllCodes.setEditable(false);
		}
		return this.textAllCodes;
	}

	protected ButtonGroup grpSequence = new ButtonGroup();;

	public JCheckBox getJcbStatusSeqinOFF() {
		if (jcbStatusSeqinOFF == null) {
			jcbStatusSeqinOFF = new JCheckBox(SequenceStatus.SEQ_OFF.name());
			grpSequence.add(jcbStatusSeqinOFF);
		}
		return jcbStatusSeqinOFF;
	}

	public JCheckBox getJcbStatusSeqinON() {
		if (jcbStatusSeqinON == null) {
			jcbStatusSeqinON = new JCheckBox(SequenceStatus.SEQ_ON.name());
			grpSequence.add(jcbStatusSeqinON);
		}
		return jcbStatusSeqinON;
	}

	public JCheckBox getJcbStatusSeqOFF() {
		if (jcbStatusSeqOFF == null) {
			jcbStatusSeqOFF = new JCheckBox(SequenceStatus.OFF.name());
			grpSequence.add(jcbStatusSeqOFF);
		}
		return jcbStatusSeqOFF;
	}

	public JCheckBox getJcbStatusSeqREADY() {
		if (jcbStatusSeqREADY == null) {
			jcbStatusSeqREADY = new JCheckBox(SequenceStatus.READY.name());
			grpSequence.add(jcbStatusSeqREADY);
		}
		return jcbStatusSeqREADY;
	}

	public JButton getButtonSetStatus() {
		if (buttonSetStatus == null) {
			buttonSetStatus = new JButton("set sequence");
			buttonSetStatus.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					buttonSetStatusActionPerformed();
				}
			});
		}
		return buttonSetStatus;
	}

	protected void buttonSetStatusActionPerformed() {
		AbstractButton b;
		Enumeration<AbstractButton> buttons = grpSequence.getElements();
		while (buttons.hasMoreElements()) {
			b = buttons.nextElement();
			if (b.isSelected()) {
				SequenceStatus ss = SequenceStatus.valueOf(b.getText());
				simulator.setSequenceStatus(ss);
			}
		}
	}
}
