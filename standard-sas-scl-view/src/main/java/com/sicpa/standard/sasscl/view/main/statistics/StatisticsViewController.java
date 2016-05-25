package com.sicpa.standard.sasscl.view.main.statistics;

import static com.sicpa.standard.client.common.utils.PropertiesUtils.extractAndDo;
import static java.lang.Boolean.parseBoolean;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.sasscl.business.statistics.IStatistics;
import com.sicpa.standard.sasscl.business.statistics.IStatisticsKeyToViewDescriptorMapping;
import com.sicpa.standard.sasscl.business.statistics.StatisticsResetEvent;
import com.sicpa.standard.sasscl.controller.view.event.LineSpeedEvent;
import com.sicpa.standard.sasscl.model.statistics.StatisticsKey;
import com.sicpa.standard.sasscl.model.statistics.StatisticsValues;
import com.sicpa.standard.sasscl.model.statistics.ViewStatisticsDescriptor;

public class StatisticsViewController implements IStatisticsViewListener {

	private static final int UPDATE_VIEW_DELAY = 100;
	private static final String STATISTICS_VISIBLITY_PREFIX = "statistics.view.visible.key.";

	private StatisticsViewModel model;
	private IStatistics statistics;
	private IStatisticsKeyToViewDescriptorMapping descriptorMapping;
	private boolean totalVisible;
	private boolean lineSpeedVisible;

	private Timer timer;

	public StatisticsViewController() {
		this(new StatisticsViewModel());
	}

	public StatisticsViewController(StatisticsViewModel model) {
		this.model = model;
		timer = new Timer("updateStats", true);
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				timerTick();
			}
		}, 1000, UPDATE_VIEW_DELAY);
	}

	private void timerTick() {
		updateStats();
		updateVisibility();
		model.notifyModelChanged();
	}

	@Subscribe
	public void handleLineSpeedEvent(LineSpeedEvent evt) {
		model.setSpeed(evt.getLine(), evt.getSpeed());
		model.notifyModelChanged();
	}

	private void updateVisibility() {
		model.setLineSpeedVisible(lineSpeedVisible);
		model.setTotalVisible(totalVisible);
	}

	private void updateStats() {
		if (statistics.getValues() == null) {
			return;
		}

		model.setTotal(getTotal());
		updateStatByLine();
		model.setUptime(statistics.getUptime());
	}

	private void updateStatByLine() {
		StatisticsValues values = statistics.getValues();
		for (Entry<StatisticsKey, Integer> entry : values.getMapValues().entrySet()) {
			if (entry.getKey() != StatisticsKey.TOTAL) {
				for (ViewStatisticsDescriptor desc : getViewKeysForAModelKey(entry.getKey())) {
					String line = entry.getKey().getLine();
					int qty = entry.getValue();
					desc.setLine(line);
					model.setStatistics(desc, qty);
				}
			}
		}
	}

	private Collection<ViewStatisticsDescriptor> getViewKeysForAModelKey(StatisticsKey key) {
		return descriptorMapping.getDescriptor(key);
	}

	private int getTotal() {
		StatisticsValues values = statistics.getValues();
		Integer total = values.get(StatisticsKey.TOTAL);
		if (total == null) {
			total = 0;
		}
		return total;
	}

	public StatisticsViewModel getModel() {
		return model;
	}

	public void setStatistics(IStatistics statistics) {
		this.statistics = statistics;
	}

	public void setDescriptorMapping(IStatisticsKeyToViewDescriptorMapping descriptorMapping) {
		this.descriptorMapping = descriptorMapping;
	}

	@Subscribe
	public void handleStatisticsReset(StatisticsResetEvent evt) {
		model.reset();
	}

	public void setAllProperties(Properties allProperties) {
		extractAndDo(STATISTICS_VISIBLITY_PREFIX, allProperties,
				(statsKey, visibility) -> model.setStatisticsVisible(statsKey, parseBoolean(visibility)));
	}

	public void setLineSpeedVisible(boolean lineSpeedVisible) {
		this.lineSpeedVisible = lineSpeedVisible;
	}

	public void setTotalVisible(boolean totalVisible) {
		this.totalVisible = totalVisible;
	}
}
