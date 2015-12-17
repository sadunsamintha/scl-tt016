package com.sicpa.standard.sasscl.business.statistics;

import com.sicpa.standard.sasscl.model.statistics.StatisticsValues;

public class StatisticsRestoredEvent {

	StatisticsValues statsValues;

	public StatisticsRestoredEvent(StatisticsValues statsValues) {
		this.statsValues = statsValues;
	}

	public StatisticsValues getStatsValues() {
		return statsValues;
	}
}
