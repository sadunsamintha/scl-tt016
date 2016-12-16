package com.sicpa.standard.sasscl.view.main.statistics;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.client.common.view.mvc.AbstractView;
import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.sasscl.model.statistics.ViewStatisticsDescriptor;
import com.sicpa.standard.sasscl.view.LanguageSwitchEvent;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.text.NumberFormat;
import java.util.*;
import java.util.Map.Entry;

import static java.util.Collections.sort;

@SuppressWarnings("serial")
public class StatisticsView extends AbstractView<IStatisticsViewListener, StatisticsViewModel> {

	// map < line index , map < stats descriptor , panel > >
	private final Map<String, Map<ViewStatisticsDescriptor, SingleStatsPanel>> statsPanelByDescriptorByLine = new HashMap<>();

	private final Map<Integer, LineSpeedPanel> panelSpeedByLineIndex = new TreeMap<>();

	private JLabel labelTitle;
	private JLabel labelUptime;

	private JPanel panelLineStats;
	private JPanel panelLineSpeed;

	private NumberFormat percentFormatter;

	private final String UPTIME_FORMATTER_PATTERN = "HH'h'mm'm'ss's'";
	private UptimeFormatter uptimeFormatter = new UptimeFormatter(UPTIME_FORMATTER_PATTERN);

	private int statDescriptorCounter = 0;
	private SingleStatsPanel panelTotal;

	public StatisticsView() {
		percentFormatter = NumberFormat.getNumberInstance();
		percentFormatter.setMinimumFractionDigits(2);
		percentFormatter.setMaximumFractionDigits(2);
		initGUI();
		handleLanguageSwitch(null);
	}

	private void initGUI() {
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

		if (isRebuildNeeded()) {
			removeAllStatsFromScreen();
		}

		buildStatsPanel();
		buildSpeedPanel();
		setUptime();

		updateElementVisibility();
	}

	private void updateElementVisibility() {
		getPanelLineSpeed().setVisible(model.isLineSpeedVisible());
		getPanelTotal().setVisible(model.isTotalVisible());

		for (Entry<String, Map<ViewStatisticsDescriptor, SingleStatsPanel>> e : statsPanelByDescriptorByLine.entrySet()) {
			for (Entry<ViewStatisticsDescriptor, SingleStatsPanel> e2 : e.getValue().entrySet()) {
				SingleStatsPanel panel = e2.getValue();
				String statsKey = e2.getKey().getKey();
				panel.setVisible(model.isStatisticVisible(statsKey));
			}
		}
	}

	private void setUptime() {
		getLabelUptime().setText(uptimeFormatter.format(model.getUptime()));
	}

	private void buildSpeedPanel() {
		if (model.getLineSpeed().size() != model.getLineIndexes().size()) {
			// do not build the panel until all speed are available to make sure they are sorted by alpha because of the
			// treemap used in the model
			return;
		}

		for (Entry<Integer, String> entry : model.getLineSpeed().entrySet()) {
			LineSpeedPanel panel = getOrCreateSpeedPanelByLineIndex(entry.getKey());
			panel.setLineSpeed(entry.getValue());
		}
	}

	private LineSpeedPanel getOrCreateSpeedPanelByLineIndex(int lineId) {
		LineSpeedPanel panel = panelSpeedByLineIndex.get(lineId);
		if (panel == null) {
			panel = new LineSpeedPanel(lineId);
			panelSpeedByLineIndex.put(lineId, panel);
			getPanelLineSpeed().add(panel, "wrap,pushx,growx");
		}
		return panel;
	}

	private void buildStatsPanel() {
		for (String lineIndex : model.getLineIndexes()) {
			buildLineStatsPanel(lineIndex);
		}
		updateTotal();
	}

	private void buildLineStatsPanel(String lineIndex) {
		int total = getTotalCountForLine(lineIndex);

		for (Entry<ViewStatisticsDescriptor, Integer> entry : getSortedStatsDescriptorList(lineIndex)) {
			int qty = entry.getValue();
			updateSingleStatsForALine(entry.getKey(), lineIndex, qty, total);
		}
	}

	private Map<ViewStatisticsDescriptor, SingleStatsPanel> getOrCreatePanelsByStatKeyForALine(String lineIndex) {
		return statsPanelByDescriptorByLine.computeIfAbsent(lineIndex, k -> new HashMap<>());
	}

	private int getTotalCountForLine(String lineIndex) {
		int total = 0;

		for (Entry<ViewStatisticsDescriptor, Integer> entry : getSortedStatsDescriptorList(lineIndex)) {
			if (entry.getKey().isCountTowardsTotal()) {
				total += entry.getValue();
			}
		}

		return total;
	}

	private List<Entry<ViewStatisticsDescriptor, Integer>> getSortedStatsDescriptorList(String index) {
		List<Entry<ViewStatisticsDescriptor, Integer>> list = new ArrayList<>();
		list.addAll(model.getStatistics(index).entrySet());
		sort(list, createComparatorByStatsViewIndex());
		return list;
	}

	private Comparator<Entry<ViewStatisticsDescriptor, Integer>> createComparatorByStatsViewIndex() {
		return (e1, e2) -> e1.getKey().getIndex() < e2.getKey().getIndex() ? -1 : 1;
	}

	private boolean isRebuildNeeded() {
		return statDescriptorCounter != 0 && model.getStatisticsDescriptorCount() != statDescriptorCounter;
	}

	private void updateSingleStatsForALine(ViewStatisticsDescriptor desc, String lineIndex, int qty, int total) {
		SingleStatsPanel panel = getOrCreateSingleStatsPanel(lineIndex, desc);
		panel.setCount(qty);
		updateTotalPercentage(panel, qty, total);
	}

	private void updateTotalPercentage(SingleStatsPanel panel, int qty, int total) {
		if (total > 0) {
			float percent = 100f * qty / total;
			panel.setPercent(percentFormatter.format(percent) + "%");
		} else {
			panel.setPercent("-");
		}
	}

	private SingleStatsPanel getOrCreateSingleStatsPanel(String lineIndex, ViewStatisticsDescriptor desc) {
		Map<ViewStatisticsDescriptor, SingleStatsPanel> panelsForLineIndex = getOrCreatePanelsByStatKeyForALine(lineIndex);

		SingleStatsPanel panel = panelsForLineIndex.get(desc);
		if (panel == null) {
			panel = new SingleStatsPanel(desc);
			statDescriptorCounter++;
			panelsForLineIndex.put(desc, panel);
			getPanelLineStats().add(panel, "wrap,pushx,growx");
		}
		return panel;
	}

	public SingleStatsPanel getPanelTotal() {
		if (panelTotal == null) {
			ViewStatisticsDescriptor desc = new ViewStatisticsDescriptor(SicpaColor.BLUE_MEDIUM, "stats.display.total",
					999, false);
			desc.setLine("total");
			panelTotal = new SingleStatsPanel(desc);
			panelTotal.getLabelPercent().setVisible(false);
		}
		return panelTotal;
	}

	private void updateTotal() {
		getPanelTotal().setCount(model.getTotal());
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
		ThreadUtils.invokeLater(this::removeAllStatsFromScreen);
	}

	private void removeAllStatsFromScreen() {
		getLabelTitle().setText(Messages.get("statistics.title"));
		statDescriptorCounter = 0;
		statsPanelByDescriptorByLine.clear();
		panelSpeedByLineIndex.clear();
		getPanelLineStats().removeAll();
		getPanelLineSpeed().removeAll();
		uptimeFormatter = new UptimeFormatter(UPTIME_FORMATTER_PATTERN);
	}
}
