package com.sicpa.standard.sasscl.view.monitoring;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.DefaultRowSorter;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang.time.DateUtils;
import org.jfree.data.time.Second;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.common.util.Messages;
import com.sicpa.standard.gui.components.buttons.shape.DirectionButton;
import com.sicpa.standard.gui.components.buttons.shape.DirectionButton.Direction;
import com.sicpa.standard.gui.components.renderers.SicpaTableCellRenderer;
import com.sicpa.standard.gui.components.table.BeanReaderJTable;
import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.sasscl.common.utils.ComparatorUtils;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.statistics.StatisticsKey;
import com.sicpa.standard.sasscl.monitoring.MonitoringService;
import com.sicpa.standard.sasscl.monitoring.statistics.MonitoredProductStatisticsValues;
import com.sicpa.standard.sasscl.monitoring.statistics.incremental.IncrementalStatistics;
import com.sicpa.standard.sasscl.monitoring.statistics.production.ProductionStatistics;
import com.sicpa.standard.sasscl.view.chart.TimeChart;
import com.sicpa.standard.sasscl.view.chart.TimeChart.TypeChart;

public class ProductionStatisticsPanel extends AbstractMonitoringEventPanel {

	private static final Logger logger= LoggerFactory.getLogger(ProductionStatisticsPanel.class);

	private static final long serialVersionUID = 1L;

	public ProductionStatisticsPanel() {
		initGUI();
		getScroll().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				scrollMouseClicked(e);
			}
		});
	}

	protected void scrollMouseClicked(final MouseEvent evt) {
		if (evt.getClickCount() != 2) {
			return;
		}

		Point p = evt.getPoint();

		p = SwingUtilities.convertPoint(getScroll(), p, getTable());

		int row = getTable().rowAtPoint(p);
		if (row != -1) {
			ProductionStatistics stats = getTable().getObjectAtRow(row);
			Date start = stats.getStartTime();
			Date stop = DateUtils.ceiling(DateUtils.addSeconds(stats.getStopTime(), 1), Calendar.SECOND);
			queryAndShowChart(start, stop);
		}
	}

	protected void queryAndShowChart(final Date start, final Date stop) {
		List<IncrementalStatistics> list = getIncrementalStatistics(start, stop);
		createAndshowChart(list);
	}

	protected List<IncrementalStatistics> getIncrementalStatistics(final Date start, final Date stop) {
		return MonitoringService.getIncrementalStatistics(start, stop);
	}

	protected List<ProductionStatistics> getProductionStatistics(final Date start, final Date stop) {
		return MonitoringService.getProductionStatistics(start, stop);
	}

	protected void createAndshowChart(final List<IncrementalStatistics> list) {

		Set<Date> markers = new HashSet<Date>();
		Map<StatisticsKey, Map<Date, Integer>> mapSeries = new HashMap<StatisticsKey, Map<Date, Integer>>();

		generateDataForChart(markers, mapSeries, list);
		String[] seriesName = genereateSeriesName(mapSeries);
		Map<Date, Integer>[] series = generateSeries(mapSeries);

		JPanel panel = new JPanel(new MigLayout("fill"));
		panel.add(createChart(seriesName, series, markers), "grow,push,wrap");
		JButton b = new DirectionButton(Direction.LEFT);
		b.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				getBusyPanel().setView(getMainPanel());
			}
		});
		panel.add(b, "w 85,h 35");
		getBusyPanel().setView(panel);
	}

	/**
	 * 
	 * @param markers
	 *            list of markers that will be populated
	 * @param series
	 *            map of serie that will be populated
	 * @param list
	 *            data input
	 */
	protected void generateDataForChart(final Set<Date> markers, final Map<StatisticsKey, Map<Date, Integer>> series,
			final List<IncrementalStatistics> list) {

		for (IncrementalStatistics incrStats : list) {

			for (Entry<StatisticsKey, Integer> entry : incrStats.getProductsStatistics().getValues().entrySet()) {
				Map<Date, Integer> aSerie = series.get(entry.getKey());

				if (aSerie == null) {
					aSerie = new TreeMap<Date, Integer>();
					series.put(entry.getKey(), aSerie);
				}

				Integer aStat = aSerie.get(incrStats.getStopTime());
				if (aStat == null) {
					aStat = 0;
				}
				aStat += entry.getValue();
				aSerie.put(incrStats.getStopTime(), aStat);
				markers.add(incrStats.getStopTime());
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected Map<Date, Integer>[] generateSeries(final Map<StatisticsKey, Map<Date, Integer>> series) {
		// -1 because we do not show the serie "total"
		Map<Date, Integer>[] array = new Map[series.size() - 1];
		Map<Date, Integer> serieTotal = series.get(StatisticsKey.TOTAL);
		int i = 0;
		for (Entry<StatisticsKey, Map<Date, Integer>> entry : series.entrySet()) {
			Map<Date, Integer> aSerie = entry.getValue();
			if (!entry.getKey().equals(StatisticsKey.TOTAL)) {
				aSerie = createPercentageSeries(serieTotal, aSerie);
				array[i] = aSerie;
				i++;
			}
		}
		return array;
	}

	protected String[] genereateSeriesName(final Map<StatisticsKey, Map<Date, Integer>> series) {
		String[] seriesName = new String[series.size()];
		int i = 0;
		for (Entry<StatisticsKey, Map<Date, Integer>> entry : series.entrySet()) {
			if (!entry.getKey().equals(StatisticsKey.TOTAL)) {
				seriesName[i] = entry.getKey().toString();
				i++;
			}
		}
		return seriesName;
	}

	protected JComponent createChart(final String[] seriesName, final Map<Date, Integer>[] array,
			final Set<Date> markers) {
		TimeChart chart = new TimeChart();
		chart.setTitle(Messages.get("label.quality.overview"));
		chart.setyAxisLabel("%");
		chart.setTypechart(TypeChart.line);
		chart.setMarkers(markers);
		chart.setData(Second.class, seriesName, array);
		chart.setColor(SicpaColor.RED, SicpaColor.GREEN_DARK);
		return chart.getPanel();
	}

	protected Map<Date, Integer> createPercentageSeries(final Map<Date, Integer> total,
			final Map<Date, Integer> toTransform) {

		Map<Date, Integer> res = new TreeMap<Date, Integer>();

		for (Entry<Date, Integer> entry : total.entrySet()) {
			Integer value = toTransform.get(entry.getKey());
			if (value == null) {
				value = 0;
			}
			value = (value * 100) / entry.getValue();
			res.put(entry.getKey(), value);
		}
		return res;
	}

	@Override
	@SuppressWarnings("unchecked")
	public BeanReaderJTable<ProductionStatistics> getTable() {
		if (this.table == null) {

			this.table = new BeanReaderJTable<ProductionStatistics>(new String[] { "startTime", "stopTime",
					"productsStatistics", "errors", "productionParameters" }, new String[] {
					Messages.get("label.start"), Messages.get("label.stop"), Messages.get("label.statistics"),
					Messages.get("label.errors"), Messages.get("label.parameters") }) {

				private static final long serialVersionUID = 1L;

				@Override
				public boolean contains(final int x, final int y) {
					return false;
				}

				@Override
				public boolean contains(final Point p) {
					return false;
				}
			};

			this.table.getColumnModel().getColumn(0).setPreferredWidth(45);
			this.table.getColumnModel().getColumn(1).setPreferredWidth(45);
			this.table.getColumnModel().getColumn(2).setPreferredWidth(150);
			this.table.getColumnModel().getColumn(3).setPreferredWidth(150);
			this.table.getColumnModel().getColumn(4).setPreferredWidth(150);

			ProductionStatisticsTableCellRenderer renderer = new ProductionStatisticsTableCellRenderer();
			this.table.getColumnModel().getColumn(0).setCellRenderer(renderer);
			this.table.getColumnModel().getColumn(1).setCellRenderer(renderer);
			this.table.getColumnModel().getColumn(2).setCellRenderer(renderer);
			this.table.getColumnModel().getColumn(3).setCellRenderer(renderer);
			this.table.getColumnModel().getColumn(4).setCellRenderer(renderer);

			((DefaultRowSorter<?, ?>) this.table.getRowSorter()).setComparator(0, new ComparatorUtils.DateComparator());
			((DefaultRowSorter<?, ?>) this.table.getRowSorter()).setComparator(1, new ComparatorUtils.DateComparator());

			this.table.getRenderers().clear();

			sortByDate();
		}
		return this.table;
	}

	@Override
	protected void requestAndHandleEvents(final Date from, final Date to) {
		final Collection<ProductionStatistics> list = getProductionStatistics(from, to);
		if (list != null) {
			ThreadUtils.invokeLater(new Runnable() {
				@Override
				public void run() {
					getTable().addRow(list);
				}
			});
		}
	}

	public static class ProductionStatisticsTableCellRenderer extends SicpaTableCellRenderer {

		protected SimpleDateFormat dateFormatter;

		{
			String pattern = null;
			try {
				pattern = Messages.get("date.pattern.statistics.production");
				this.dateFormatter = new SimpleDateFormat(pattern);
			} catch (Exception e) {
				logger.error("invalid or pattern not found {}", pattern);
				this.dateFormatter = new SimpleDateFormat("'<html>'yyyy-MM-dd'<br>'HH:mm:ss");
			}
		}

		private static final long serialVersionUID = 1L;

		@SuppressWarnings("unchecked")
		@Override
		public Component getTableCellRendererComponent(final JTable table, final Object value,
				final boolean isSelected, final boolean hasFocus, final int row, final int column) {

			BeanReaderJTable<ProductionStatistics> t = (BeanReaderJTable<ProductionStatistics>) table;

			JLabel label = (JLabel) super
					.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			label.setFont(label.getFont().deriveFont(10f));

			if (value instanceof Date) {
				label.setText(this.dateFormatter.format((Date) value));
			} else if (value instanceof List<?>) {
				String text = "<html>";
				for (Object o : (List<?>) value) {
					text += o + "<br>";
				}
				label.setText(text);
			} else if (value instanceof ProductionParameters) {
				String text = "";
				ProductionParameters param = (ProductionParameters) value;
				text += param.getProductionMode() + " - " + param.getSku();
				if (param.getBarcode() != null) {
					text += " - " + param.getBarcode();
				}
				label.setText(text);
			} else if (value instanceof MonitoredProductStatisticsValues) {
				MonitoredProductStatisticsValues stat = (MonitoredProductStatisticsValues) value;
				String text = "";
				if (stat.getValues() != null) {
					for (Entry<StatisticsKey, Integer> entry : stat.getValues().entrySet()) {

						Integer offset = stat.getMapOffset().get(entry.getKey());
						if (offset == null) {
							offset = 0;
						}
						int delta = entry.getValue() - offset;
						text += entry.getKey() + "-" + delta + "  ";
					}
				}
				label.setText(text);
			}

			if (row < t.getRowCount()) {
				ProductionStatistics data = t.getObjectAtRow(row);
				if (data != null && data.getErrors() != null) {
					if (data.getErrors().size() > 0) {
						label.setForeground(Color.RED);
					}
				}
			}

			return label;
		}
	}
}
