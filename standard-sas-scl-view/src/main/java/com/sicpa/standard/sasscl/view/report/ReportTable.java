package com.sicpa.standard.sasscl.view.report;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Point;
import java.awt.print.PrinterException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.DefaultRowSorter;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.gui.components.renderers.SicpaTableCellRenderer;
import com.sicpa.standard.gui.components.scroll.SmallScrollBar;
import com.sicpa.standard.gui.components.table.BeanReaderJTable;
import com.sicpa.standard.gui.utils.ThreadUtils;

public class ReportTable extends JPanel {

	private static final long serialVersionUID = 1L;

	private static Logger logger= LoggerFactory.getLogger(ReportTable.class);

	protected JScrollPane scroll;
	protected JTable currentTable;

	public ReportTable() {
		initGUI();
	}

	private void initGUI() {
		setLayout(new BorderLayout());
		add(SmallScrollBar.createLayerSmallScrollBar(getScroll()), BorderLayout.CENTER);
	}

	public JScrollPane getScroll() {
		if (this.scroll == null) {
			this.scroll = new JScrollPane(new JPanel());
		}
		return this.scroll;
	}

	public void setData(final Map<ReportKey, ReportData> map, final boolean groupByProduct, final boolean detailed) {
		final List<ReportDataWrapper> data = new ArrayList<>();
		setReportDataWrapperData(map, data);

		ThreadUtils.invokeLater(() -> {
            String[] fields;
            String[] title;

            if (groupByProduct) {
                fields = getFieldsGroupByProduct();
                title = getTitleGroupByProduct();
            } else {
                fields = getFields();
                title = getTitle();
            }

            BeanReaderJTable<ReportDataWrapper> table = new BeanReaderJTable<ReportDataWrapper>(fields, title) {

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

            currentTable = table;
            table.setFont(table.getFont().deriveFont(10f));

            table.addRow(data);
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            TableRowSorter<TableModel> sorter = (TableRowSorter<TableModel>) table.getRowSorter();

            if (groupByProduct) {
                setTableColumnPropertiesGroupByProduct(table, sorter, detailed);
            } else {
                setTableColumnProperties(table, sorter);
            }
            getScroll().setViewportView(table);

            table.getRowSorter().toggleSortOrder(0);
        });
	}

    protected String[] getFieldsGroupByProduct() {
        return new String[] { "period", "productionMode", "sku", "runningTime", "total", "quality", "good", "bad" };
    }

    protected String[] getTitleGroupByProduct() {
        return new String[] { Messages.get("production.report.period"),
                Messages.get("production.report.productionMode"), Messages.get("production.report.sku"),
                Messages.get("production.report.runningTime"), Messages.get("production.report.total"),
                Messages.get("production.report.quality"), Messages.get("production.report.good"),
                Messages.get("production.report.bad") };
    }

    protected String[] getFields() {
        return new String[] { "Period", "RunningTime", "Total", "Quality", "Good", "Bad" };
    }

    protected String[] getTitle() {
        return new String[] { Messages.get("production.report.period"),
                Messages.get("production.report.runningTime"), Messages.get("production.report.total"),
                Messages.get("production.report.quality"), Messages.get("production.report.good"),
                Messages.get("production.report.bad") };
    }

    protected ReportDataWrapper createReportDataWrapper() {
        return new ReportDataWrapper();
    }

    protected void setReportDataWrapperData(Map<ReportKey, ReportData> map, List<ReportDataWrapper> data) {
        for (Entry<ReportKey, ReportData> entry : map.entrySet()) {
            ReportDataWrapper reportDataWrapper = createReportDataWrapper();

            reportDataWrapper.setPeriod(entry.getKey().getDate());
            reportDataWrapper.setProductionMode(entry.getKey().getProductionMode());
            reportDataWrapper.setSku(entry.getKey().getSku());
            reportDataWrapper.setProductNumber(entry.getValue().getGood(), entry.getValue().getBad());
            reportDataWrapper.setRunningTime(entry.getValue().getRunningTime());

            data.add(reportDataWrapper);
        }
    }

	protected void setTableColumnProperties(BeanReaderJTable<ReportDataWrapper> table,
                                            TableRowSorter<TableModel> sorter) {
        table.getColumnModel().getColumn(1).setCellRenderer(new TimeCellRenderer());
        sorter.setComparator(1, new IntComparator());
    }

	protected void setTableColumnPropertiesGroupByProduct(BeanReaderJTable<ReportDataWrapper> table,
                                                          TableRowSorter<TableModel> sorter, boolean detailed) {
        table.getColumnModel().getColumn(3).setCellRenderer(new TimeCellRenderer());
        sorter.setComparator(3, new IntComparator());

        if (detailed) {
            table.getColumnModel().getColumn(0).setPreferredWidth(200);
        } else {
            table.getColumnModel().getColumn(0).setPreferredWidth(80);
        }

        table.getColumnModel().getColumn(1).setPreferredWidth(80);
        table.getColumnModel().getColumn(2).setPreferredWidth(80);
        table.getColumnModel().getColumn(3).setPreferredWidth(80);
        table.getColumnModel().getColumn(4).setPreferredWidth(30);
        table.getColumnModel().getColumn(5).setPreferredWidth(30);
        table.getColumnModel().getColumn(6).setPreferredWidth(30);
        table.getColumnModel().getColumn(7).setPreferredWidth(30);
    }

	public static class IntComparator implements Comparator<Integer> {
		@Override
		public int compare(final Integer o1, final Integer o2) {
			return o1.compareTo(o2);
		}
	}

	public static class TimeCellRenderer extends SicpaTableCellRenderer {

		private static final long serialVersionUID = 1L;

		@Override
		public Component getTableCellRendererComponent(final JTable table, final Object value,
				final boolean isSelected, final boolean hasFocus, final int row, final int column) {

			int secByHour = 3600;
			int secByDay = 3600 * 24;

			int time = (Integer) value;

			int days = time / secByDay;
			time -= days * secByDay;

			int hours = time / secByHour;
			time -= secByHour * hours;

			int mins = time / 60;
			time -= mins * 60;

			int sec = time;
			String text = "";

			if (days > 0) {
				text += days + "d";
			}
			if (hours > 0 || days > 0) {
				text += hours + "h";
			}
			if (mins > 0 || hours > 0 || days > 0) {
				text += mins + "m";
			}
			text += sec + "s";

			return super.getTableCellRendererComponent(table, text, isSelected, hasFocus, row, column);
		}
	}

	public void printReport() {
		if (currentTable != null) {
			try {
				currentTable.print();
			} catch (PrinterException e) {
				logger.error("", e);
			}
		}
	}
}
