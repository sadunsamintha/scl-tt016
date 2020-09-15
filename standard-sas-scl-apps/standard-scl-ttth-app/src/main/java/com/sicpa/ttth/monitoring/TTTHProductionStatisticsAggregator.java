package com.sicpa.ttth.monitoring;

import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.monitoring.statistics.incremental.IncrementalStatistics;
import com.sicpa.standard.sasscl.view.monitoring.ProductionStatisticsAggregator;
import com.sicpa.standard.sasscl.view.report.ReportData;
import com.sicpa.ttth.view.report.TTTHReportData;

public class TTTHProductionStatisticsAggregator extends ProductionStatisticsAggregator {

    @Override
    protected ReportData createReportData() {
        return new TTTHReportData();
    }

    @Override
    protected void setReportData(ReportData reportData, IncrementalStatistics incrStats) {
        super.setReportData(reportData, incrStats);

        String batchJobId = "N/A";
        String productionModeDesc = incrStats.getProductionParameters()
            .getProductionMode().getDescription();

        if (productionModeDesc.equals(ProductionMode.STANDARD.getDescription())
            || productionModeDesc.equals(ProductionMode.REFEED_NORMAL.getDescription())
            || productionModeDesc.equals(ProductionMode.REFEED_CORRECTION.getDescription())) {
            batchJobId = incrStats.getBatch();
        }

        ((TTTHReportData) reportData).setBatchJobId(batchJobId);


    }
}
