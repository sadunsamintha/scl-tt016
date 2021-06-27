package com.sicpa.tt016.view.report;

import com.sicpa.standard.sasscl.view.report.ReportDataWrapper;

import lombok.Getter;
import lombok.Setter;

public class TT016ReportDataWrapper extends ReportDataWrapper {

    private int inkDetected;
    private int ejectedProducer;
    
    @Setter
    @Getter
    private int gross;
    
    @Setter
    @Getter
    private int nett;

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

    public void setStatisticsData(int good, int bad, int inkDetected, int ejectedProducer) {
        this.good = good;
        this.bad = bad;
        this.inkDetected = inkDetected;
        this.ejectedProducer = ejectedProducer;
    }

    @Override
    public void setProductNumber(int good, int bad) {
    }

    @Override
    public int getTotal() {
        return good + bad + ejectedProducer;
    }
}
