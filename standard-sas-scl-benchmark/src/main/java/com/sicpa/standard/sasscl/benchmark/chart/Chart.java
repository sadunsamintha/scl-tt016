package com.sicpa.standard.sasscl.benchmark.chart;

import org.jfree.chart.JFreeChart;

public abstract class Chart {
	protected JFreeChart chart;

	protected ChartPanel panel;

	public ChartPanel getPanel() {
		return this.panel;
	}

	public void setPanel(final ChartPanel panel) {
		this.panel = panel;
	}

	public JFreeChart getChart() {
		return this.chart;
	}

	public void setChart(final JFreeChart chart) {
		this.chart = chart;
	}
}