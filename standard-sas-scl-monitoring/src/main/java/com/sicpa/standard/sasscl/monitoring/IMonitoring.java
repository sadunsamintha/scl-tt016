package com.sicpa.standard.sasscl.monitoring;

import com.sicpa.standard.sasscl.monitoring.statistics.incremental.IncrementalStatistics;
import com.sicpa.standard.sasscl.monitoring.system.AbstractSystemEvent;
import com.sicpa.standard.sasscl.monitoring.system.event.BasicSystemEvent;

import java.util.Date;
import java.util.List;

public interface IMonitoring {

	void addSystemEvent(AbstractSystemEvent event);
	
	List<BasicSystemEvent> getSystemEvent(Date from,Date to);
	
	List<IncrementalStatistics> getIncrementalStatistics(Date from,Date to);
}
