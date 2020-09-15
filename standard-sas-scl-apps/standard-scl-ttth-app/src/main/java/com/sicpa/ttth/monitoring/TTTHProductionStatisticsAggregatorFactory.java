package com.sicpa.ttth.monitoring;

import com.sicpa.standard.sasscl.view.monitoring.ProductionStatisticsAggregator;
import com.sicpa.standard.sasscl.view.monitoring.ProductionStatisticsAggregatorFactory;

public class TTTHProductionStatisticsAggregatorFactory extends ProductionStatisticsAggregatorFactory {

    @Override
    protected ProductionStatisticsAggregator createInstance() {
        return new TTTHProductionStatisticsAggregator();
    }

}
