package com.sicpa.standard.sasscl.view.monitoring;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.DefaultRowSorter;
import javax.swing.JLabel;
import javax.swing.JTable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.common.util.Messages;
import com.sicpa.standard.gui.components.renderers.SicpaTableCellRenderer;
import com.sicpa.standard.gui.components.table.BeanReaderJTable;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.sasscl.common.utils.ComparatorUtils;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.statistics.StatisticsKey;
import com.sicpa.standard.sasscl.monitoring.MonitoringService;
import com.sicpa.standard.sasscl.monitoring.statistics.MonitoredProductStatisticsValues;
import com.sicpa.standard.sasscl.monitoring.statistics.production.ProductionStatistics;

@SuppressWarnings("serial")
public class ProductionStatisticsPanel extends AbstractMonitoringEventPanel {

	private static final Logger logger = LoggerFactory.getLogger(ProductionStatisticsPanel.class);

	public ProductionStatisticsPanel() {
		initGUI();
	}

	private List<ProductionStatistics> getProductionStatistics(final Date start, final Date stop) {
		return MonitoringService.getProductionStatistics(start, stop);
	}

	@Override
	@SuppressWarnings("unchecked")
	public BeanReaderJTable<ProductionStatistics> getTable() {
		if (table == null) {

			table = new BeanReaderJTable<ProductionStatistics>(new String[] { "startTime", "stopTime",
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

			table.getColumnModel().getColumn(0).setPreferredWidth(45);
			table.getColumnModel().getColumn(1).setPreferredWidth(45);
			table.getColumnModel().getColumn(2).setPreferredWidth(150);
			table.getColumnModel().getColumn(3).setPreferredWidth(150);
			table.getColumnModel().getColumn(4).setPreferredWidth(150);

			ProductionStatisticsTableCellRenderer renderer = new ProductionStatisticsTableCellRenderer();
			table.getColumnModel().getColumn(0).setCellRenderer(renderer);
			table.getColumnModel().getColumn(1).setCellRenderer(renderer);
			table.getColumnModel().getColumn(2).setCellRenderer(renderer);
			table.getColumnModel().getColumn(3).setCellRenderer(renderer);
			table.getColumnModel().getColumn(4).setCellRenderer(renderer);

			((DefaultRowSorter<?, ?>) table.getRowSorter()).setComparator(0, new ComparatorUtils.DateComparator());
			((DefaultRowSorter<?, ?>) table.getRowSorter()).setComparator(1, new ComparatorUtils.DateComparator());

			table.getRenderers().clear();

			sortByDate();
		}
		return table;
	}

	@Override
	protected void requestAndHandleEvents(Date from, Date to) {
		final Collection<ProductionStatistics> list = getProductionStatistics(from, to);
		if (list != null) {
			ThreadUtils.invokeLater(() -> getTable().addRow(list));
		}
	}

	public static class ProductionStatisticsTableCellRenderer extends SicpaTableCellRenderer {

		SimpleDateFormat dateFormatter;

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
