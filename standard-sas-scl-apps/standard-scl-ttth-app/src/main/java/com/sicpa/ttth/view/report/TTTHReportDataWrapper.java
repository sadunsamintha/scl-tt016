package com.sicpa.ttth.view.report;

import com.sicpa.standard.sasscl.view.report.ReportDataWrapper;

public class TTTHReportDataWrapper extends ReportDataWrapper {

    private String batchJobId;

    public String getBatchJobId() {
        return batchJobId;
    }

    public void setBatchJobId(String batchJobId) {
        this.batchJobId = batchJobId;
    }
}
