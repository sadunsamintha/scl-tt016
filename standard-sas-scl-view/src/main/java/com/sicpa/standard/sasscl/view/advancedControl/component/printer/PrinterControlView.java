package com.sicpa.standard.sasscl.view.advancedControl.component.printer;

import static java.util.Collections.sort;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import com.sicpa.standard.client.common.i18n.Messages;
import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.client.common.view.mvc.AbstractView;
import com.sicpa.standard.sasscl.view.advancedControl.component.IControlViewComponent;

@SuppressWarnings("serial")
public class PrinterControlView extends AbstractView<IPrinterViewControlListener, PrinterControlViewModel> implements
		IControlViewComponent {

	private class PrinterPanel extends JPanel {
		JButton buttonOn;
		JButton buttonOff;
		String printerId;

		public PrinterPanel(String printerId) {
			this.printerId = printerId;
			setLayout(new MigLayout(""));
			add(new JLabel(printerId), "wrap");
			add(getButtonOn());
			add(getButtonOff());
		}

		public JButton getButtonOff() {
			if (buttonOff == null) {
				buttonOff = new JButton(Messages.get("printer.command.switch.off"));
				buttonOff.addActionListener(e -> fireSwitchOff(printerId));
			}
			return buttonOff;
		}

		public JButton getButtonOn() {
			if (buttonOn == null) {
				buttonOn = new JButton(Messages.get("printer.command.switch.on"));
				buttonOn.addActionListener(e -> fireSwitchOn(printerId));
			}
			return buttonOn;
		}
	}

	public PrinterControlView() {
		setLayout(new MigLayout(""));
	}

	private void fireSwitchOff(String printerId) {
		listeners.forEach(l -> l.switchOff(printerId));
	}

	private void fireSwitchOn(String printerId) {
		listeners.forEach(l -> l.switchOn(printerId));
	}

	@Override
	public void modelChanged() {
		removeAll();
		addHeader();
		addAllPrinterPanels();
		revalidate();
	}

	private void addHeader() {
		add(new JLabel(Messages.get("printer.command.title")), "spanx , split 2");
		add(new JSeparator(JSeparator.HORIZONTAL), "growx,push");
	}

	private void addAllPrinterPanels() {
		List<String> printers = new ArrayList<>(model.getPrinters());
		sort(printers);
		printers.forEach(printerId -> addPrinterPanel(printerId));
	}

	private void addPrinterPanel(String printerId) {
		PrinterPanel panel = new PrinterPanel(printerId);
		add(panel, "wrap");
	}

	@Override
	public String getConstraints() {
		return "pushx,growx";
	}
}
