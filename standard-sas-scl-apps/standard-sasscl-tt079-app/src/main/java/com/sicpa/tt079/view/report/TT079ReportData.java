package com.sicpa.tt079.view.report;

import com.sicpa.standard.sasscl.view.report.ReportData;

public class TT079ReportData extends ReportData {

    private int inkDetected;

    @Override
    public void addData(ReportData data) {
        super.addData(data);

        TT079ReportData reportData = (TT079ReportData) data;

        inkDetected += reportData.getInkDetected();
    }

    @Override
    public String toString() {
        return "ReportData [bad=" + bad + ", good=" + good + ", inkDetected=" + inkDetected + ", running=" + runningTime + "]";
    }

    public int getInkDetected() {
        return inkDetected;
    }

    public void setInkDetected(int inkDetected) {
        this.inkDetected = inkDetected;
    }
}