package com.sicpa.tt016.view.report;

import com.sicpa.standard.sasscl.view.report.ReportData;

public class TT016ReportData extends ReportData {

    private int inkDetected;
    private int ejectedProducer;

    @Override
    public void addData(ReportData data) {
        super.addData(data);

        TT016ReportData reportData = (TT016ReportData) data;

        inkDetected += reportData.getInkDetected();
        ejectedProducer += reportData.getEjectedProducer();
    }

    @Override
    public String toString() {
        return "ReportData [bad=" + bad + ", good=" + good + ", inkDetected=" + inkDetected + ", ejectedProducer="
                + ejectedProducer + ", running=" + runningTime + "]";
    }

    public int getInkDetected() {
        return inkDetected;
    }

    public void setInkDetected(int inkDetected) {
        this.inkDetected = inkDetected;
    }

    public int getEjectedProducer() {
        return ejectedProducer;
    }

    public void setEjectedProducer(int ejectedProducer) {
        this.ejectedProducer = ejectedProducer;
    }
}