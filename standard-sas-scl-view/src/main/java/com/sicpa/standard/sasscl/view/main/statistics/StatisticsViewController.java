package com.sicpa.standard.sasscl.view.main.statistics;

import java.util.Map.Entry;
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

	protected StatisticsViewModel model;
	protected IStatistics statistics;
	protected IStatisticsKeyToViewDescriptorMapping descriptorMapping;

	protected Timer timer;

	public StatisticsViewController(StatisticsViewModel model) {
		this.model = model;
		timer = new Timer("updateStats", true);
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				updateStats();
			}
		}, 1000, 100);
	}

	public StatisticsViewController() {
		this(new StatisticsViewModel());
	}

	@Subscribe
	public void handleLineSpeedEvent(LineSpeedEvent evt) {
		model.setSpeed(evt.getLine(), evt.getSpeed());
		model.notifyModelChanged();
	}

	protected void updateStats() {
		StatisticsValues values = statistics.getValues();
		if (values == null) {
			return;
		}
		Integer total = values.get(StatisticsKey.TOTAL);
		if (total == null) {
			total = 0;
		}
		model.setTotal(total);

		for (Entry<StatisticsKey, Integer> entry : values.getMapValues().entrySet()) {
			if (entry.getKey() != StatisticsKey.TOTAL) {
				for (ViewStatisticsDescriptor desc : descriptorMapping.getDescriptor(entry.getKey())) {
					String line = entry.getKey().getLine();
					desc.setLine(line);
					model.setStatistics(desc, entry.getValue());
				}
			}
		}

		model.setUptime(statistics.getUptime());

		model.notifyModelChanged();
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
}
