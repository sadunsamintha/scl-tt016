package com.sicpa.tt079.monitoring;

import com.sicpa.standard.sasscl.view.monitoring.ProductionStatisticsAggregator;
import com.sicpa.standard.sasscl.view.monitoring.ProductionStatisticsAggregatorFactory;

public class TT079ProductionStatisticsAggregatorFactory extends ProductionStatisticsAggregatorFactory {

    @Override
    protected ProductionStatisticsAggregator createInstance() {
        return new TT079ProductionStatisticsAggregator();
    }
}
