package com.sicpa.tt016.view.report;

import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.gui.components.table.BeanReaderJTable;
import com.sicpa.standard.sasscl.view.report.ReportData;
import com.sicpa.standard.sasscl.view.report.ReportDataWrapper;
import com.sicpa.standard.sasscl.view.report.ReportKey;
import com.sicpa.standard.sasscl.view.report.ReportTable;

import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.util.List;
import java.util.Map;

public class TT016ReportTable extends ReportTable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
    protected String[] getFieldsGroupByProduct() {
        return new String[] { "period", "productionMode", "sku", "runningTime", "total", "good", "bad", "inkDetected",
                "ejectedProducer", "gross", "nett" };
    }

    @Override
    protected String[] getTitleGroupByProduct() {
        return new String[] { Messages.get("production.report.period"),
                Messages.get("production.report.productionMode"), Messages.get("production.report.sku"),
                Messages.get("production.report.runningTime"), Messages.get("production.report.total"),
                Messages.get("production.report.good"), Messages.get("production.report.bad"),
                Messages.get("production.report.inkDetected"), Messages.get("production.report.ejectedProducer"),
                Messages.get("production.report.gross"), Messages.get("production.report.nett") 
        };
    }

    @Override
    protected String[] getFields() {
        return new String[] { "Period", "RunningTime", "Total", "Good", "Bad", "inkDetected", "ejectedProducer","gross","nett"};
    }

    @Override
    protected String[] getTitle() {
        return new String[] { Messages.get("production.report.period"),
                Messages.get("production.report.runningTime"), Messages.get("production.report.total"),
                Messages.get("production.report.good"), Messages.get("production.report.bad"),
                Messages.get("production.report.inkDetected"), Messages.get("production.report.ejectedProducer"),
                Messages.get("production.report.gross"), Messages.get("production.report.nett")
        };
    }

    @Override
    protected ReportDataWrapper createReportDataWrapper() {
        return new TT016ReportDataWrapper();
    }

    @Override
    protected void setReportDataWrapperData(Map<ReportKey, ReportData> map, List<ReportDataWrapper> data) {
        for (Map.Entry<ReportKey, ReportData> entry : map.entrySet()) {
            TT016ReportDataWrapper reportDataWrapper = (TT016ReportDataWrapper) createReportDataWrapper();

            reportDataWrapper.setPeriod(entry.getKey().getDate());
            reportDataWrapper.setProductionMode(entry.getKey().getProductionMode());
            reportDataWrapper.setSku(entry.getKey().getSku());
            reportDataWrapper.setRunningTime(entry.getValue().getRunningTime());

            TT016ReportData tt016Entry = (TT016ReportData) entry.getValue();

            reportDataWrapper.setStatisticsData(tt016Entry.getGood(), tt016Entry.getBad(), tt016Entry.getInkDetected(),
                    tt016Entry.getEjectedProducer());
            reportDataWrapper.setGross(tt016Entry.getGross());
            reportDataWrapper.setNett(tt016Entry.getNett());
            data.add(reportDataWrapper);
        }
    }

    @Override
    protected void setTableColumnPropertiesGroupByProduct(BeanReaderJTable<ReportDataWrapper> table,
                                                          TableRowSorter<TableModel> sorter, boolean detailed) {
        table.getColumnModel().getColumn(3).setCellRenderer(new TimeCellRenderer());
        sorter.setComparator(3, new IntComparator());

        if (detailed) {
            table.getColumnModel().getColumn(0).setPreferredWidth(200);
        } else {
            table.getColumnModel().getColumn(0).setPreferredWidth(80);
        }

        table.getColumnModel().getColumn(1).setPreferredWidth(70);
        table.getColumnModel().getColumn(2).setPreferredWidth(70);
        table.getColumnModel().getColumn(3).setPreferredWidth(70);
        table.getColumnModel().getColumn(4).setPreferredWidth(30);
        table.getColumnModel().getColumn(5).setPreferredWidth(30);
        table.getColumnModel().getColumn(6).setPreferredWidth(30);
        table.getColumnModel().getColumn(7).setPreferredWidth(30);
        table.getColumnModel().getColumn(8).setPreferredWidth(30);
    }
}