package com.sicpa.standard.sasscl.benchmark.chart;

import java.awt.Color;
import java.awt.Dimension;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.DefaultIntervalXYDataset;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;

import com.sicpa.standard.gui.utils.Pair;

public class TimeChart extends Chart {

	public static enum TypeChart {
		bar, line;
	}

	private TypeChart typeChart = TypeChart.line;
	private XYDataset dataSet;

	private String title, xAxis, yAxis;
	private List<Pair<Integer, String>> markers;

	public TimeChart(final Map<Integer, Integer> data, final String title, final String x, final String y,
			final List<Pair<Integer, String>> markers, final TypeChart typeChart) {
		this.title = title;
		this.xAxis = x;
		this.yAxis = y;
		this.markers = markers;
		this.typeChart = typeChart;
		setData(data);
	}

	public void setData(final Map<Integer, Integer> dataMap) {
		if (this.typeChart == TypeChart.line) {
			this.dataSet = new DefaultXYDataset();
			double[][] dataArray = new double[2][dataMap.size()];
			int index = 0;
			for (Entry<Integer, Integer> entry : dataMap.entrySet()) {
				dataArray[0][index] = entry.getKey();
				dataArray[1][index] = entry.getValue();
				index++;
			}
			((DefaultXYDataset) this.dataSet).addSeries(this.title, dataArray);
		} else {
			this.dataSet = new DefaultIntervalXYDataset();
			double[][] dataArray = new double[6][dataMap.size()];
			int index = 0;
			for (Entry<Integer, Integer> entry : dataMap.entrySet()) {
				dataArray[0][index] = entry.getKey();
				dataArray[1][index] = entry.getKey();
				dataArray[2][index] = entry.getKey();
				dataArray[3][index] = entry.getValue();
				dataArray[4][index] = entry.getValue();
				dataArray[5][index] = entry.getValue();
				index++;
			}
			((DefaultIntervalXYDataset) this.dataSet).addSeries(this.title, dataArray);
		}

		initChart();
	}

	private void initChart() {
		if (this.typeChart == TypeChart.line) {
			this.chart = ChartFactory.createXYLineChart(this.title, this.xAxis, this.yAxis, this.dataSet,
					PlotOrientation.VERTICAL, true, true, true);
		} else {
			this.chart = ChartFactory.createXYBarChart(this.title, this.xAxis, false, this.yAxis,
					(IntervalXYDataset) this.dataSet, PlotOrientation.VERTICAL, true, true, true);
		}

		this.panel = new ChartPanel(this.chart);
		this.panel.setPreferredSize(new Dimension(800, 400));

		this.panel.getChart().setBackgroundPaint(Color.WHITE);

		XYPlot xyplot = this.chart.getXYPlot();
		if (this.markers != null) {
			for (Pair<Integer, String> entry : this.markers) {
				// add a labelled marker
				Marker aMarker = new ValueMarker(entry.getValue1());
				aMarker.setPaint(Color.BLUE);
				aMarker.setLabel(entry.getValue2());
				aMarker.setLabelAnchor(RectangleAnchor.TOP_LEFT);
				aMarker.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
				xyplot.addDomainMarker(aMarker);
			}
		}
	}
}
