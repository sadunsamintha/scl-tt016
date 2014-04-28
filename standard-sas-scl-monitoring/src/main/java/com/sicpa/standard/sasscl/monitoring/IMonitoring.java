package com.sicpa.standard.sasscl.monitoring;

import java.util.Date;
import java.util.List;

import com.sicpa.standard.sasscl.monitoring.statistics.incremental.IncrementalStatistics;
import com.sicpa.standard.sasscl.monitoring.statistics.production.ProductionStatistics;
import com.sicpa.standard.sasscl.monitoring.system.AbstractSystemEvent;
import com.sicpa.standard.sasscl.monitoring.system.event.BasicSystemEvent;

public interface IMonitoring {

	void addSystemEvent(AbstractSystemEvent event);
	
	List<BasicSystemEvent> getSystemEvent(Date from,Date to);
	
	List<ProductionStatistics> getProductionStatistics(Date from,Date to);
	
	List<IncrementalStatistics> getIncrementalStatistics(Date from,Date to);
}
