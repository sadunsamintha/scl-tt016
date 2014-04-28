package com.sicpa.standard.sasscl.monitoring.system.event;

import com.sicpa.standard.sasscl.model.statistics.StatisticsValues;
import com.sicpa.standard.sasscl.monitoring.system.AbstractSystemEvent;
import com.sicpa.standard.sasscl.monitoring.system.SystemEventLevel;
import com.sicpa.standard.sasscl.monitoring.system.SystemEventType;

public class StatisticsSystemEvent extends AbstractSystemEvent {

	protected StatisticsValues values;

	public StatisticsSystemEvent(final StatisticsValues values) {
		super(SystemEventLevel.INFO, SystemEventType.STATISTICS_CHANGED);
		this.values = values;
	}

	public StatisticsSystemEvent(final StatisticsValues values, final SystemEventType type) {
		super(SystemEventLevel.INFO, type);
		this.values = values;
	}

	private static final long serialVersionUID = 1L;

	@Override
	public StatisticsValues getMessage() {
		return this.values;
	}

}
