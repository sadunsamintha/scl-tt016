package com.sicpa.ttth.monitoring.statistics.incremental;

import com.sicpa.standard.sasscl.monitoring.statistics.MonitoringStatistics;
import com.sicpa.standard.sasscl.monitoring.statistics.incremental.IncrementalStatistics;

public class TTTHIncrementalStatistics extends IncrementalStatistics {

    private static final long serialVersionUID = 1L;

    private String batch;

    public TTTHIncrementalStatistics() {
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }
}
