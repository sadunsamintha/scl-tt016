package com.sicpa.tt016.monitoring;

import com.sicpa.standard.sasscl.view.monitoring.IProductionStatisticsAggregator;
import com.sicpa.standard.sasscl.view.monitoring.ProductionStatisticsAggregator;
import com.sicpa.standard.sasscl.view.monitoring.ProductionStatisticsAggregatorFactory;

public class TT016ProductionStatisticsAggregatorFactory extends ProductionStatisticsAggregatorFactory {

    @Override
    public IProductionStatisticsAggregator getInstance() {
        return super.getInstance();
    }

    @Override
    protected ProductionStatisticsAggregator createInstance() {
        return new TT016ProductionStatisticsAggregator();
    }
}
