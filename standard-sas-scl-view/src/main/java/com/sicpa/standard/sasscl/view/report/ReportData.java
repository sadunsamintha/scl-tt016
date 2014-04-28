package com.sicpa.standard.sasscl.view.report;

public class ReportData {
	protected int runningTime;
	protected int good;
	protected int bad;

	public int getRunningTime() {
		return this.runningTime;
	}

	public void setRunningTime(final int running) {
		this.runningTime = running;
	}

	public int getGood() {
		return this.good;
	}

	public void setGood(final int good) {
		this.good = good;
	}

	public int getBad() {
		return this.bad;
	}

	public void setBad(final int bad) {
		this.bad = bad;
	}

	public void addData(final ReportData data) {
		this.good += data.good;
		this.bad += data.bad;
		this.runningTime += data.runningTime;
	}

	@Override
	public String toString() {
		return "ReportData [bad=" + this.bad + ", good=" + this.good + ", running=" + this.runningTime + "]";
	}
}
