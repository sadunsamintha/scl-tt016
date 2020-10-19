package com.sicpa.tt079.view.report;

import java.text.NumberFormat;

import com.sicpa.standard.sasscl.view.report.ReportDataWrapper;

public class TT079ReportDataWrapper extends ReportDataWrapper {

    private int inkDetected;
    private String blobRate;

    public int getInkDetected() {
        return inkDetected;
    }

    public void setInkDetected(int inkDetected) {
        this.inkDetected = inkDetected;
    }

    public String getBlobRate() {
		return blobRate;
	}

	public void setStatisticsData(int good, int bad, int inkDetected) {
        this.good = good;
        this.bad = bad;
        this.inkDetected = inkDetected;
    }
    
    public void setBlobRate(final int good, final int bad, final int inkDetected) {
		this.good = good;
		this.bad = bad;
		this.inkDetected = inkDetected;
		if (good + bad != 0) {
			this.blobRate = NumberFormat.getPercentInstance().format((float) this.inkDetected / (this.good + this.bad));
		} else {
			this.blobRate = "";
		}
	}
}
