package com.sicpa.standard.sasscl.view.main.statistics;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import net.miginfocom.swing.MigLayout;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.view.mvc.AbstractView;
import com.sicpa.standard.common.util.Messages;
import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.sasscl.model.statistics.ViewStatisticsDescriptor;
import com.sicpa.standard.sasscl.view.LanguageSwitchEvent;

@SuppressWarnings("serial")
public class StatisticsView extends AbstractView<IStatisticsViewListener, StatisticsViewModel> {

	// map < line index , map < stats descriptor , panel > >
	protected final Map<String, Map<ViewStatisticsDescriptor, SingleStatsPanel>> mapDescriptors = new HashMap<String, Map<ViewStatisticsDescriptor, SingleStatsPanel>>();

	// map < line index , panel >
	protected final Map<String, LineSpeedPanel> mapLineSpeed = new TreeMap<String, LineSpeedPanel>();

	protected JLabel labelTitle;
	protected JLabel labelUptime;

	protected JPanel panelLineStats;
	protected JPanel panelLineSpeed;

	protected NumberFormat percentFormater;

	protected UptimeFormater uptimeFormater = new UptimeFormater("HH'h'mm'm'ss's'");

	public StatisticsView() {
		percentFormater = NumberFormat.getNumberInstance();
		percentFormater.setMinimumFractionDigits(2);
		percentFormater.setMaximumFractionDigits(2);
		initGUI();
		handleLanguageSwitch(null);
	}

	protected void initGUI() {
		setLayout(new MigLayout(""));
		add(getLabelTitle(), "pushx,spanx , split 2");
		add(new JSeparator(), "growx");
		add(getPanelLineStats(), "pushx,grow,wrap");
		add(getPanelTotal(), "spanx,pushx,growx");
		add(getPanelLineSpeed(), "gap top 10, pushx,grow,wrap");
		add(getLabelUptime(), "");
	}

	public JLabel getLabelUptime() {
		if (labelUptime == null) {
			labelUptime = new JLabel();
		}
		return labelUptime;
	}

	@Override
	public void modelChanged() {
		if (model == null || !isShowing()) {
			return;
		}

		if (statDescriptorCounter != 0 && isRebuildNeeded()) {
			statDescriptorCounter = 0;
			refreshAll();
			return;
		}

		buildStatsPanel();
		buildSpeedPanel();
		getLabelUptime().setText(uptimeFormater.format(model.getUptime()));
	}

	protected void buildSpeedPanel() {
		if (model.getLineSpeed().size() != model.getLineIndexes().size()) {
			// do not build the panel until all speed are available to make sure they are sorted by alpha because of the
			// treemap used in the model
			return;
		}

		for (Entry<String, String> entry : model.getLineSpeed().entrySet()) {
			LineSpeedPanel panel = mapLineSpeed.get(entry.getKey());
			if (panel == null) {
				panel = new LineSpeedPanel(entry.getKey());
				mapLineSpeed.put(entry.getKey(), panel);
				getPanelLineSpeed().add(panel, "wrap,pushx,growx");
			}
			panel.setLineSpeed(entry.getValue());
		}
	}

	protected void buildStatsPanel() {
		for (String index : model.getLineIndexes()) {
			Map<ViewStatisticsDescriptor, SingleStatsPanel> panels = mapDescriptors.get(index);
			int total = 0;
			if (panels == null) {
				panels = new HashMap<ViewStatisticsDescriptor, SingleStatsPanel>();
				mapDescriptors.put(index, panels);
			}

			for (Entry<ViewStatisticsDescriptor, Integer> entry : getSortedDescriptorList(index)) {
				total += entry.getValue();
			}

			for (Entry<ViewStatisticsDescriptor, Integer> entry : getSortedDescriptorList(index)) {
				updateStats(entry.getKey(), entry.getValue(), panels, total);
			}
		}
		updateTotal();
	}

	protected List<Entry<ViewStatisticsDescriptor, Integer>> getSortedDescriptorList(String index) {
		List<Entry<ViewStatisticsDescriptor, Integer>> list = new ArrayList<Map.Entry<ViewStatisticsDescriptor, Integer>>();
		list.addAll(model.getStatistics(index).entrySet());
		Collections.sort(list, new Comparator<Entry<ViewStatisticsDescriptor, Integer>>() {
			@Override
			public int compare(Entry<ViewStatisticsDescriptor, Integer> e1, Entry<ViewStatisticsDescriptor, Integer> e2) {
				return e1.getKey().getIndex() < e2.getKey().getIndex() ? -1 : 1;
			}
		});
		return list;
	}

	protected boolean isRebuildNeeded() {
		return model.getStatisticsDescriptorCount() != statDescriptorCounter;
	}

	protected int statDescriptorCounter = 0;

	protected void updateStats(ViewStatisticsDescriptor desc, int qty,
			Map<ViewStatisticsDescriptor, SingleStatsPanel> panelsForLineIndex, int total) {
		SingleStatsPanel panel = panelsForLineIndex.get(desc);
		if (panel == null) {
			panel = new SingleStatsPanel(desc);
			statDescriptorCounter++;
			panelsForLineIndex.put(desc, panel);
			getPanelLineStats().add(panel, "wrap,pushx,growx");
		}
		panel.setCount("" + qty);

		if (total > 0) {
			float percent = 100f * qty / total;
			panel.setPercent(percentFormater.format(percent) + "%");
		} else {
			panel.setPercent("-");
		}
	}

	protected SingleStatsPanel panelTotal;

	public SingleStatsPanel getPanelTotal() {
		if (panelTotal == null) {
			ViewStatisticsDescriptor desc = new ViewStatisticsDescriptor(SicpaColor.BLUE_MEDIUM, "stats.display.total",
					999);
			panelTotal = new SingleStatsPanel(desc);
			panelTotal.labelPercent.setVisible(false);
		}
		return panelTotal;
	}

	protected void updateTotal() {
		getPanelTotal().setCount("" + model.getTotal());
	}

	public static class SingleStatsPanel extends JPanel {

		protected JLabel labelCount;
		protected JLabel labelPercent;

		public SingleStatsPanel(ViewStatisticsDescriptor descriptor) {
			setLayout(new MigLayout("fill, inset 0 0 0 0"));

			String text = "";
			if (descriptor.getLine() != null) {
				text = Messages.get("line." + descriptor.getLine()) + " - ";
			}
			text += Messages.get(descriptor.getKey());

			JLabel label = new JLabel(text);
			label.setForeground(descriptor.getColor());
			add(label, " w 250!");

			labelPercent = new JLabel("-");
			labelCount = new JLabel("0");

			labelPercent.setForeground(descriptor.getColor());
			labelCount.setForeground(descriptor.getColor());

			add(labelCount, "w 75!,left");
			add(labelPercent, "w 75!, left,pushx");
		}

		void setCount(String count) {
			labelCount.setText(count);
		}

		void setPercent(String percent) {
			labelPercent.setText(percent);
		}
	}

	public static class LineSpeedPanel extends JPanel {

		JLabel labelSpeedValue;

		public LineSpeedPanel(String line) {
			setLayout(new MigLayout("fill, inset 0 0 0 0"));

			labelSpeedValue = new JLabel();

			String text = Messages.get("line" + line) + " - " + Messages.get("stats.display.speed");
			add(new JLabel(text), "w 250");
			add(labelSpeedValue, "spanx, left,pushx");
//			add(new JLabel(Messages.get("stats.display.speed.unit")), "left");
		}

		public void setLineSpeed(String speed) {
			labelSpeedValue.setText("" + speed);
		}
	}

	public JLabel getLabelTitle() {
		if (labelTitle == null) {
			labelTitle = new JLabel();
		}
		return labelTitle;
	}

	public JPanel getPanelLineStats() {
		if (panelLineStats == null) {
			panelLineStats = new JPanel(new MigLayout("inset 0 0 0 0"));
		}
		return panelLineStats;
	}

	public JPanel getPanelLineSpeed() {
		if (panelLineSpeed == null) {
			panelLineSpeed = new JPanel(new MigLayout("inset 0 0 0 0"));
		}
		return panelLineSpeed;
	}

	@Subscribe
	public void handleLanguageSwitch(LanguageSwitchEvent evt) {
		refreshAll();
	}

	protected void refreshAll() {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				getLabelTitle().setText(Messages.get("statistics.title"));
				mapDescriptors.clear();
				mapLineSpeed.clear();
				getPanelLineStats().removeAll();
				getPanelLineSpeed().removeAll();
			}
		});
	}
}
