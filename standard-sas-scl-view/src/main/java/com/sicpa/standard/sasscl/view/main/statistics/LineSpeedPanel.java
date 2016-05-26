package com.sicpa.standard.sasscl.view.main.statistics;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.sicpa.standard.client.common.i18n.Messages;

import net.miginfocom.swing.MigLayout;


@SuppressWarnings("serial")
public class LineSpeedPanel extends JPanel {

	private JLabel labelSpeedValue;

	public LineSpeedPanel(int line) {
		setLayout(new MigLayout("fill, inset 0 0 0 0"));

		labelSpeedValue = new JLabel();

		String text = Messages.get("line" + line) + " - " + Messages.get("stats.display.speed");
		add(new JLabel(text), "w 250");
		add(labelSpeedValue, "spanx, left,pushx");
	}

	public void setLineSpeed(String speed) {
		labelSpeedValue.setText("" + speed);
	}
}