package com.sicpa.standard.sasscl.skucheck.view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.common.util.Messages;
import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.sasscl.skucheck.SkuCheckFacade;
import com.sicpa.standard.sasscl.skucheck.acquisition.GroupAcquisitionType;

public class StatisticsPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	protected int devicesCount;
	protected List<DeviceStatisticsPanel> devicesPanels;
	protected JLabel labelQuality;
	protected JLabel labelTotal;
	protected SkuCheckFacade facade;
	protected JPanel panelTotal;

	public StatisticsPanel(int devicesCount) {
		this.devicesCount = devicesCount;
		devicesPanels = new ArrayList<DeviceStatisticsPanel>();
		initGUI();
		new Timer(getTimerDelay(), new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				update();
			}
		}).start();
	}

	protected int getTimerDelay() {
		return 1000;
	}

	private void initGUI() {
		setLayout(new MigLayout(""));
		for (int i = 0; i < devicesCount; i++) {
			DeviceStatisticsPanel panel = new DeviceStatisticsPanel();
			devicesPanels.add(panel);
			add(new JLabel(Messages.get("sku.check.devices." + i)), "top");
			add(panel, "grow,wrap");
		}
		add(new JLabel(Messages.get("sku.check.acquisition.count")), "spanx , split 2");
		add(getLabelTotal(), "gaptop 15");
		add(getPanelTotal(), "grow,spanx");
	}

	public JLabel getLabelQuality() {
		if (labelQuality == null) {
			labelQuality = new JLabel("-");
		}
		return labelQuality;
	}

	protected void update() {
		if (facade == null || facade.getAcquisitionGroupCount() <= 0) {
			return;
		}

		for (int i = 0; i < devicesPanels.size(); i++) {
			devicesPanels.get(i).update(facade.getStatistics(i), facade.getAcquisitionGroupCount());
		}

		getLabelTotal().setText("" + facade.getAcquisitionGroupCount());

		List<Wrapper> list = new ArrayList<Wrapper>();
		for (Entry<GroupAcquisitionType, Integer> entry : facade.getStatistics().entrySet()) {
			Wrapper w = new Wrapper();
			w.type = entry.getKey();
			if (entry.getValue() != null) {
				w.value = entry.getValue();
			}
			list.add(w);
		}
		Collections.sort(list);

		getPanelTotal().removeAll();
		for (Wrapper w : list) {
			Color foreground;
			if (w.type == GroupAcquisitionType.SINGLE_CODE) {
				foreground = SicpaColor.GREEN_DARK;
			} else {
				foreground = SicpaColor.RED;
			}

			// --text
			JLabel label = new JLabel(AcquisitionTypeLangMapping.getMessage(w.type));
			label.setForeground(foreground);
			getPanelTotal().add(label);

			// ---value
			float percent = 1f * w.value / facade.getAcquisitionGroupCount();
			label = new JLabel(w.value + " ( " + NumberFormat.getPercentInstance().format(percent) + " )");
			label.setForeground(foreground);
			getPanelTotal().add(label, "wrap");
		}
	}

	public JLabel getLabelTotal() {
		if (labelTotal == null) {
			labelTotal = new JLabel("0");
		}
		return labelTotal;
	}

	public JPanel getPanelTotal() {
		if (panelTotal == null) {
			panelTotal = new JPanel(new MigLayout("inset 0 0 0 0"));
		}
		return panelTotal;
	}

	protected static class Wrapper implements Comparable<Wrapper> {

		GroupAcquisitionType type;
		int value;

		@Override
		public int compareTo(Wrapper o) {
			int thisVal = this.value;
			int anotherVal = o.value;
			return (thisVal < anotherVal ? 1 : (thisVal == anotherVal ? 0 : -1));
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((type == null) ? 0 : type.hashCode());
			result = prime * result + value;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Wrapper other = (Wrapper) obj;
			if (type != other.type)
				return false;
			if (value != other.value)
				return false;
			return true;
		}
	}

	public void setFacade(SkuCheckFacade facade) {
		this.facade = facade;
	}

	public SkuCheckFacade getFacade() {
		return facade;
	}
}
