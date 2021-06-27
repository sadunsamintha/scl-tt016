package com.sicpa.tt016.monitoring.impl;

import java.util.Date;
import java.util.Map;

import com.sicpa.standard.monitor.IMonitorTypesMapping;
import com.sicpa.standard.sasscl.model.statistics.StatisticsKey;
import com.sicpa.standard.sasscl.monitoring.impl.Monitoring;
import com.sicpa.standard.sasscl.monitoring.statistics.incremental.IncrementalStatistics;
import com.sicpa.tt016.monitoring.statistics.incremental.TT016IncrementalStatistics;
import com.sicpa.tt016.monitoring.system.event.GrossNettCountSystemEvent;

public class TT016Monitoring extends Monitoring {

    public TT016Monitoring(IMonitorTypesMapping mapping) {
        super(mapping);
    }
    
	protected void populateDoNotSaveEventList() {
		super.populateDoNotSaveEventList();
		doNotSaveEvents.add(GrossNettCountSystemEvent.GROSS_NETT_COUNT_CHANGED);
	}
	

    @Override
    protected void createNewIncrementalStatistics(boolean savingAtMidnight) {
        IncrementalStatistics previous = incrementalStatistics;
        incrementalStatistics = new TT016IncrementalStatistics();
        incrementalStatistics.setSubsystemId(subsystemIdProvider.get());
        incrementalStatistics.setProductionParameters(productionParameters);
        incrementalStatistics.setStartTime(savingAtMidnight ? getMidnightNextDay(previous.getStartTime()) : new Date());
        ((TT016IncrementalStatistics)incrementalStatistics).setNettCount(0);
        ((TT016IncrementalStatistics)incrementalStatistics).setGrossCount(0);
        if (previous != null) {
            Map<StatisticsKey, Integer> mapValues;
            if (previous.getProductsStatistics().getValues().size() == 0) {
                mapValues = previous.getProductsStatistics().getMapOffset();
            } else {
                mapValues = previous.getProductsStatistics().getValues();
            }
            incrementalStatistics.getProductsStatistics().setMapOffset(mapValues);
        }
    }

}
