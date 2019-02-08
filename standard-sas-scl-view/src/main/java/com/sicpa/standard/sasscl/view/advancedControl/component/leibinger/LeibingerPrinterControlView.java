package com.sicpa.standard.sasscl.view.advancedControl.component.leibinger;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.client.common.view.mvc.AbstractView;
import com.sicpa.standard.sasscl.view.LanguageSwitchEvent;
import com.sicpa.standard.sasscl.view.advancedControl.component.IControlViewComponent;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.sort;

@SuppressWarnings("serial")
public class LeibingerPrinterControlView extends AbstractView<ILeibingerPrinterViewControlListener, LeibingerPrinterControlViewModel> implements
		IControlViewComponent {


	private Integer[] leibingerUserLevels = { 0, 1, 2, 3, 4 };

	private static final String LABEL_SEPARATOR = " : ";

	private class PrinterPanel extends JPanel {
		JComboBox<Integer> userLevels;
		String printerId;

		public PrinterPanel(String leibingerUserLevelLabel, String printerId) {
			this.printerId = printerId;
			setLayout(new MigLayout(""));
			add(new JLabel(leibingerUserLevelLabel), "wrap");
			add(getComboBoxUserlevels(), "w 100");
		}

		public JComboBox getComboBoxUserlevels() {
			if (userLevels == null) {
				userLevels =  new JComboBox(leibingerUserLevels);
				userLevels.setSelectedIndex(0);
				userLevels.addActionListener(e -> fireChangeLeibingerUserLevel(printerId, getUserLevel(e)));
			}
			return userLevels;
		}
	}

	private int getUserLevel(ActionEvent e) {
		JComboBox cb = (JComboBox)e.getSource();
		Integer userLevel = (Integer)cb.getSelectedItem();
		return userLevel;
	}

	public LeibingerPrinterControlView() {
		setLayout(new MigLayout(""));
	}

	private void fireChangeLeibingerUserLevel(String printerId, int userLevel) {
		listeners.forEach(l -> l.changeLeibingerUserLevel(printerId, userLevel));
	}


	@Override
	public void modelChanged() {
		removeAll();
		addHeader();
		addAllPrinterPanels();
		revalidate();
	}

	private void addHeader() {
		add(new JLabel(Messages.get("leibinger.printer.command.title")), "spanx , split 2");
		add(new JSeparator(JSeparator.HORIZONTAL), "growx,push");
	}

	private void addAllPrinterPanels() {
		List<String> printers = new ArrayList<>(model.getPrinters());
		sort(printers);
		printers.forEach(printerId -> addPrinterPanel(printerId));
	}

	private void addPrinterPanel(String printerId) {
		String label = buildLabelText(printerId);
		PrinterPanel panel = new PrinterPanel(label,printerId);
		add(panel, "wrap");
	}

	private String buildLabelText(String printerId) {
		StringBuilder sb = new StringBuilder();
		sb.append(printerId);
		sb.append(LABEL_SEPARATOR);
		sb.append(Messages.get("leibinger.printer.userLevels"));
		return sb.toString();
	}

	@Override
	public String getConstraints() {
		return "newline,growx";
	}
	
	@Subscribe
	public void handleLanguageSwitch(LanguageSwitchEvent evt) {
		modelChanged();
	}
}
