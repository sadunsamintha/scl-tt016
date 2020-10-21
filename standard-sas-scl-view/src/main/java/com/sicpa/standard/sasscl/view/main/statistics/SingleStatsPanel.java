package com.sicpa.standard.sasscl.view.main.statistics;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.sasscl.model.statistics.ViewStatisticsDescriptor;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class SingleStatsPanel extends JPanel {

	private JLabel labelCount;
	private JLabel labelPercent;
	
	public SingleStatsPanel(ViewStatisticsDescriptor descriptor) {
		setLayout(new MigLayout("fill, inset 0 0 0 0"));

		String text = "";
		text = Messages.get("line." + descriptor.getLine()) + " - ";
		text += Messages.get(descriptor.getKey());

		JLabel label = new JLabel(text);
		label.setForeground(descriptor.getColor());
		add(label, " w 280!");

		labelPercent = new JLabel("-");
		labelCount = new JLabel("0");

		labelPercent.setForeground(descriptor.getColor());
		labelCount.setForeground(descriptor.getColor());

		add(labelCount, "w 150!,left");
		add(labelPercent, "w 85!, left,pushx");
	}

	public void setCount(String count) {
		labelCount.setText(count);
	}

	public void setPercent(String percent) {
		labelPercent.setText(percent);
	}

	public JLabel getLabelCount() {
		return labelCount;
	}

	public JLabel getLabelPercent() {
		return labelPercent;
	}
}