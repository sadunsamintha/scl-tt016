package com.sicpa.standard.sasscl.monitoring;

import com.sicpa.standard.sasscl.monitoring.statistics.incremental.IncrementalStatistics;
import com.sicpa.standard.sasscl.monitoring.system.AbstractSystemEvent;
import com.sicpa.standard.sasscl.monitoring.system.event.BasicSystemEvent;

import java.util.Date;
import java.util.List;

public class MonitoringService {

	protected static IMonitoring monitoring;

	public static void set(final IMonitoring monitoring) {
		MonitoringService.monitoring = monitoring;
	}

	public static void addSystemEvent(final AbstractSystemEvent event) {
		if (monitoring != null) {
			monitoring.addSystemEvent(event);
		}
	}

	public static List<BasicSystemEvent> getSystemEventList(final Date from, final Date to) {
		List<BasicSystemEvent> res = null;
		if (monitoring != null) {
			res = monitoring.getSystemEvent(from, to);
		}
		return res;
	}

	public static List<IncrementalStatistics> getIncrementalStatistics(final Date from, final Date to) {
		List<IncrementalStatistics> res = null;
		if (monitoring != null) {
			res = monitoring.getIncrementalStatistics(from, to);
		}
		return res;
	}
}
