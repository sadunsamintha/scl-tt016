package com.sicpa.ttth.view.report;

import com.sicpa.standard.sasscl.view.report.ReportData;

public class TTTHReportData extends ReportData {

    private String batchJobId;

    @Override
    public void addData(ReportData data) {
        super.addData(data);

        TTTHReportData reportData = (TTTHReportData) data;

        batchJobId = reportData.getBatchJobId();
    }

    @Override
    public String toString() {
        return "ReportData [bad=" + bad + ", good=" + good + ", batchJobID=" + batchJobId + ", running=" + runningTime + "]";
    }

    public String getBatchJobId() {
        return batchJobId;
    }

    public void setBatchJobId(String batchJobId) {
        this.batchJobId = batchJobId;
    }
}
