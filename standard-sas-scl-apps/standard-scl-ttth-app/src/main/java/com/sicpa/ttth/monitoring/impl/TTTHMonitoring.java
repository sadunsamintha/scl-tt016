package com.sicpa.ttth.monitoring.impl;

import java.util.Date;
import java.util.Map;

import com.sicpa.standard.monitor.IMonitorTypesMapping;
import com.sicpa.standard.sasscl.model.statistics.StatisticsKey;
import com.sicpa.standard.sasscl.monitoring.impl.Monitoring;
import com.sicpa.standard.sasscl.monitoring.statistics.incremental.IncrementalStatistics;
import com.sicpa.standard.sasscl.provider.ProductBatchJobIdProvider;

public class TTTHMonitoring extends Monitoring implements ProductBatchJobIdProvider {

    public TTTHMonitoring(IMonitorTypesMapping mapping) {
        super(mapping);
    }

    @Override
    protected void createNewIncrementalStatistics(boolean savingAtMidnight) {
        IncrementalStatistics previous = incrementalStatistics;
        incrementalStatistics = new IncrementalStatistics();
        incrementalStatistics.setSubsystemId(subsystemIdProvider.get());
        incrementalStatistics.setProductionParameters(productionParameters);
        //Set the batch job id.
        incrementalStatistics.setBatch(productionParameters.getProperty(productionBatchJobId));
        incrementalStatistics.setStartTime(savingAtMidnight ? getMidnightNextDay(previous.getStartTime()) : new Date());

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
