package com.sicpa.standard.sasscl.view.chart;

import org.jfree.chart.JFreeChart;

public abstract class Chart {
	protected JFreeChart chart;

	protected ChartPanel panel;

	public ChartPanel getPanel() {
		return this.panel;
	}

	public JFreeChart getChart() {
		return this.chart;
	}
}