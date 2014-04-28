package com.sicpa.standard.sasscl.view.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Minute;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;

public class TimeChart extends Chart {

	public static enum TypeChart {
		bar, line
	};

	private String title;
	private static BasicStroke stroke = new BasicStroke(1);

	private Collection<Date> markers;
	private TypeChart typechart;

	private String yAxisLabel;
	private String xAxisLabel = "Time";

	public TimeChart() {
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	public void setyAxisLabel(final String yAxisLabel) {
		this.yAxisLabel = yAxisLabel;
	}

	public void setTypechart(final TypeChart typechart) {
		this.typechart = typechart;
	}

	public void setMarkers(final Collection<Date> markers) {
		this.markers = markers;
	}

	private TimeSeriesCollection timeseriescollection;

	@SuppressWarnings("deprecation")
	public void setData(final Class<?> typetime, final String[] seriesName, final Map<Date, Integer>... series) {

		this.timeseriescollection = new TimeSeriesCollection();
		this.timeseriescollection.setDomainIsPointsInTime(false);

		int i = 0;
		for (Map<Date, Integer> stats : series) {

			TimeSeries timeseries = null;
			if (typetime == org.jfree.data.time.Minute.class) {
				timeseries = new TimeSeries(seriesName[i], "Minute", "", typetime);
				for (Entry<Date, Integer> entry : stats.entrySet()) {
					TimeSeriesDataItem value = timeseries.getDataItem(new Minute(entry.getKey()));
					if (value == null) {
						timeseries.addOrUpdate(new Minute(entry.getKey()), entry.getValue());
					} else {
						timeseries.addOrUpdate(new Minute(entry.getKey()),
								Integer.valueOf(entry.getValue().intValue() + value.getValue().intValue()));
					}
				}
				this.timeseriescollection.addSeries(timeseries);

			} else if (typetime == org.jfree.data.time.Second.class) {
				timeseries = new TimeSeries(seriesName[i], "Second", "", typetime);
				for (Date time : stats.keySet()) {
					timeseries.addOrUpdate(new Second(time), new Integer(stats.get(time)));
				}
				this.timeseriescollection.addSeries(timeseries);

			}
			i++;
		}
		initChart(this.timeseriescollection, this.typechart);
	}

	@Override
	public ChartPanel getPanel() {
		return super.getPanel();
	}

	// public void initChart() {
	// initChart(this.dataset, this.typechart);
	// }

	@SuppressWarnings("deprecation")
	private void initChart(final XYDataset dataset, final TypeChart typechart) {

		if (typechart == TypeChart.bar) {
			this.chart = ChartFactory.createXYBarChart(this.title, this.yAxisLabel, true, this.yAxisLabel,
					(IntervalXYDataset) dataset, PlotOrientation.VERTICAL, true, true, true);
		} else {
			// line
			this.chart = ChartFactory.createTimeSeriesChart(this.title, this.xAxisLabel, this.yAxisLabel, dataset,
					true, true, true);
			// this.chart = ChartFactory.createXYLineChart("title", "Time in second", "", dataset,
			// PlotOrientation.VERTICAL, true, true, true);
		}

		// chart.addSubtitle(new TextTitle("(subtitle)"));
		this.chart.setBackgroundPaint(Color.WHITE);

		XYPlot xyplot = this.chart.getXYPlot();
		xyplot.setBackgroundPaint(Color.lightGray);
		xyplot.setRangeGridlinePaint(Color.LIGHT_GRAY);// ligne horizontale
		xyplot.setDomainGridlinePaint(Color.WHITE); // ligne verticale


		// xyplot.setBackgroundImageAlpha(0.3f);
		// xyplot.setBackgroundImageAlignment(Align.TOP_LEFT); empeche le resize
		// de l img
		xyplot.setBackgroundPaint(new Color(0, 0, 0, 0));

		if (this.markers != null) {

			for (Date markerDate : this.markers) {
				// add a labelled marker
				Second markerTime = new Second(markerDate);
				double millis = markerTime.getFirstMillisecond();
				final Marker aMarker = new ValueMarker(millis);
				aMarker.setPaint(new Color(0, 0, 255, 75));
				aMarker.setLabel("");
				aMarker.setStroke(stroke);
				aMarker.setLabelAnchor(RectangleAnchor.RIGHT);
				aMarker.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
				xyplot.addDomainMarker(aMarker);
			}
		}

		XYItemRenderer xyitemrenderer = xyplot.getRenderer();
		StandardXYToolTipGenerator standardxytooltipgenerator = new StandardXYToolTipGenerator("{0}:  {1} = {2}",
				new SimpleDateFormat("HH:mm:ss"), new DecimalFormat("0"));
		xyitemrenderer.setToolTipGenerator(standardxytooltipgenerator);
		// xyitemrenderer.setToolTipGenerator(StandardXYToolTipGenerator.
		// getTimeSeriesInstance());

		if (typechart == TypeChart.bar) {
			DateAxis dateaxis = (DateAxis) xyplot.getDomainAxis();
			dateaxis.setTickMarkPosition(DateTickMarkPosition.START);
			dateaxis.setLowerMargin(0.01D);
			dateaxis.setUpperMargin(0.01D);
		}

		this.panel = new ChartPanel(this.chart);
		this.panel.setPreferredSize(new Dimension(800, 400));
		// panel.setMinimumSize(new Dimension(800, 300));
		// panel.setMaximumSize(new Dimension(800, 300));

	}

	public void setColor(final Color... colors) {

		XYPlot xyplot = this.chart.getXYPlot();
		if (xyplot.getRenderer(0) instanceof XYLineAndShapeRenderer) {
			XYLineAndShapeRenderer r0 = (XYLineAndShapeRenderer) xyplot.getRenderer(0);
			int i = 0;
			for (Color c : colors) {
				r0.setSeriesPaint(i, c);
				i++;
			}
		}
	}

	public void removeLegend() {
		if (this.chart != null) {
			this.chart.removeLegend();
		}
	}
}
