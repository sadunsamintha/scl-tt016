package com.sicpa.tt079.monitoring;

import java.util.Map;

import com.sicpa.standard.sasscl.model.statistics.StatisticsKey;
import com.sicpa.standard.sasscl.monitoring.statistics.incremental.IncrementalStatistics;
import com.sicpa.standard.sasscl.view.monitoring.ProductionStatisticsAggregator;
import com.sicpa.standard.sasscl.view.report.ReportData;
import com.sicpa.tt079.model.statistics.TT079StatisticsKey;
import com.sicpa.tt079.view.report.TT079ReportData;

public class TT079ProductionStatisticsAggregator extends ProductionStatisticsAggregator {

    @Override
    protected ReportData createReportData() {
        return new TT079ReportData();
    }

    @Override
    protected void setReportData(ReportData reportData, IncrementalStatistics incrStats) {
        super.setReportData(reportData, incrStats);

        Integer inkDetected = 0;

        for (Map.Entry<StatisticsKey, Integer> entry : incrStats.getProductsStatistics().getValues().entrySet()) {
            if (entry.getKey().toString().endsWith(StatisticsKey.BLOB.getDescription())) {
                inkDetected += entry.getValue();
            }
        }

        ((TT079ReportData) reportData).setInkDetected(inkDetected);
    }
}
