package com.sicpa.tt016.monitoring;

import com.sicpa.standard.sasscl.model.statistics.StatisticsKey;
import com.sicpa.standard.sasscl.monitoring.statistics.incremental.IncrementalStatistics;
import com.sicpa.standard.sasscl.view.monitoring.ProductionStatisticsAggregator;
import com.sicpa.standard.sasscl.view.report.ReportData;
import com.sicpa.tt016.model.statistics.TT016StatisticsKey;
import com.sicpa.tt016.view.report.TT016ReportData;

import java.util.Map;

public class TT016ProductionStatisticsAggregator extends ProductionStatisticsAggregator {

    @Override
    protected ReportData createReportData() {
        return new TT016ReportData();
    }

    @Override
    protected void setReportData(ReportData reportData, IncrementalStatistics incrStats) {
        super.setReportData(reportData, incrStats);

        Integer inkDetected = 0;
        Integer producerEjected = 0;

        for (Map.Entry<StatisticsKey, Integer> entry : incrStats.getProductsStatistics().getValues().entrySet()) {
            if (entry.getKey().toString().endsWith(TT016StatisticsKey.INK_DETECTED.getDescription())) {
                inkDetected += entry.getValue();
            } else if (entry.getKey().toString().endsWith(TT016StatisticsKey.EJECTED_PRODUCER.getDescription())) {
                producerEjected += entry.getValue();
            }
        }

        ((TT016ReportData) reportData).setInkDetected(inkDetected);
        ((TT016ReportData) reportData).setEjectedProducer(producerEjected);
    }
}
