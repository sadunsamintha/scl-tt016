package com.sicpa.standard.sasscl.view.report;

import java.text.NumberFormat;

public class ReportDataWrapper {
	protected String period;
	protected String productionMode;
	protected String sku;
	protected int runningTime;
	protected int good;
	protected int bad;
	protected String quality;

	public String getPeriod() {
		return this.period;
	}

	public void setPeriod(final String period) {
		this.period = period;
	}

	public String getProductionMode() {
		return this.productionMode;
	}

	public void setProductionMode(final String productionMode) {
		this.productionMode = productionMode;
	}

	public String getSku() {
		return this.sku;
	}

	public void setSku(final String sku) {
		this.sku = sku;
	}

	public int getRunningTime() {
		return this.runningTime;
	}

	public void setRunningTime(final int time) {
		this.runningTime = time;
	}

	public int getTotal() {
		return this.good + this.bad;
	}

	public int getGood() {
		return this.good;
	}

	public void setProductNumber(final int good, final int bad) {
		this.good = good;
		this.bad = bad;
		if (good + bad != 0) {
			this.quality = NumberFormat.getPercentInstance().format((float) this.good / (this.good + this.bad));
		} else {
			this.quality = "";
		}
	}

	public int getBad() {
		return this.bad;
	}

	public String getQuality() {
		return this.quality;
	}
}