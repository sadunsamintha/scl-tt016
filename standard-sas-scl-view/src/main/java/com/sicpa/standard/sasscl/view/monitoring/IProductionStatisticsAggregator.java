package com.sicpa.standard.sasscl.view.monitoring;

import com.sicpa.standard.sasscl.monitoring.statistics.incremental.IncrementalStatistics;
import com.sicpa.standard.sasscl.view.report.ReportData;
import com.sicpa.standard.sasscl.view.report.ReportKey;
import com.sicpa.standard.sasscl.view.report.ReportPeriod;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IProductionStatisticsAggregator {

    void aggregate(final List<IncrementalStatistics> list, final ReportPeriod period,
                   final boolean groupByProduct, final boolean dailyDetail, Date minimumDate, Date maxDate);

    Map<ReportKey, ReportData> getMapData();
}
