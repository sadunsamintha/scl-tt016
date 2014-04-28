package com.sicpa.standard.sasscl.skucheck.view;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.sasscl.skucheck.acquisition.SingleAcquisitionType;

public class DeviceStatisticsPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public DeviceStatisticsPanel() {
		initGUI();
	}

	private void initGUI() {
		setLayout(new MigLayout("inset 0 0 0 0, gap 0 0 0 0"));
	}

	public void update(Map<SingleAcquisitionType, Integer> stats, int total) {
		// keep the previous stats until we start again
		if (!stats.isEmpty()) {
			List<Wrapper> list = new ArrayList<DeviceStatisticsPanel.Wrapper>();
			for (Entry<SingleAcquisitionType, Integer> entry : stats.entrySet()) {
				Wrapper w = new Wrapper();
				w.type = entry.getKey();
				if (entry.getValue() != null) {
					w.value = entry.getValue();
				}
				list.add(w);
			}

			Collections.sort(list);
			initGui(list, total);
		}
	}

	protected void initGui(List<Wrapper> wrappers, int total) {
		removeAll();
		for (Wrapper w : wrappers) {
			float percent = 1f * w.value / total;
			add(new JLabel(AcquisitionTypeLangMapping.getMessage(w.type)));
			add(new JLabel(w.value + "( " + NumberFormat.getPercentInstance().format(percent) + " )"),
					"gapleft 10, wrap");
		}
		revalidate();
		repaint();
	}

	protected static class Wrapper implements Comparable<Wrapper> {

		SingleAcquisitionType type;
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

}
