package com.sicpa.tt079.view.report;

import static com.sicpa.standard.client.common.security.SecurityService.hasPermission;

import java.util.List;
import java.util.Map;

import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.gui.components.table.BeanReaderJTable;
import com.sicpa.standard.sasscl.view.report.ReportData;
import com.sicpa.standard.sasscl.view.report.ReportDataWrapper;
import com.sicpa.standard.sasscl.view.report.ReportKey;
import com.sicpa.standard.sasscl.view.report.ReportTable;
import com.sicpa.tt079.model.productionParameters.TT079Permission;

public class TT079ReportTable extends ReportTable {

    @Override
    protected String[] getFieldsGroupByProduct() {
    	if (hasPermission(TT079Permission.PRODUCTION_REPORT_BLOB_RATE)) {
    		return new String[] { "period", "productionMode", "sku", "runningTime", "total", "quality", "good", "bad", "blobRate" };
    	} else {
    		return new String[] { "period", "productionMode", "sku", "runningTime", "total", "quality", "good", "bad" };
    	}
    }

    @Override
    protected String[] getTitleGroupByProduct() {
    	if (hasPermission(TT079Permission.PRODUCTION_REPORT_BLOB_RATE)) {
    		return new String[] { Messages.get("production.report.period"),
                    Messages.get("production.report.productionMode"), Messages.get("production.report.sku"),
                    Messages.get("production.report.runningTime"), Messages.get("production.report.total"),
                    Messages.get("production.report.quality"), Messages.get("production.report.good"), 
                    Messages.get("production.report.bad"), Messages.get("production.report.blobRate")
            };
    	} else {
    		return new String[] { Messages.get("production.report.period"),
                    Messages.get("production.report.productionMode"), Messages.get("production.report.sku"),
                    Messages.get("production.report.runningTime"), Messages.get("production.report.total"),
                    Messages.get("production.report.quality"), Messages.get("production.report.good"), 
                    Messages.get("production.report.bad")
            };
    	}
    }

    @Override
    protected String[] getFields() {
    	if (hasPermission(TT079Permission.PRODUCTION_REPORT_BLOB_RATE)) {
    		return new String[] { "Period", "RunningTime", "Total", "Quality", "Good", "Bad", "blobRate" };
    	} else {
    		return new String[] { "Period", "RunningTime", "Total", "Quality", "Good", "Bad" };
    	}
    }

    @Override
    protected String[] getTitle() {
    	if (hasPermission(TT079Permission.PRODUCTION_REPORT_BLOB_RATE)) {
    		return new String[] { Messages.get("production.report.period"),
                    Messages.get("production.report.runningTime"), Messages.get("production.report.total"),
                    Messages.get("production.report.quality"), Messages.get("production.report.good"), 
                    Messages.get("production.report.bad"), Messages.get("production.report.blobRate")
            };
    	} else {
    		return new String[] { Messages.get("production.report.period"),
                    Messages.get("production.report.runningTime"), Messages.get("production.report.total"),
                    Messages.get("production.report.quality"), Messages.get("production.report.good"), 
                    Messages.get("production.report.bad")
            };
    	}
    }

    @Override
    protected ReportDataWrapper createReportDataWrapper() {
        return new TT079ReportDataWrapper();
    }

    @Override
    protected void setReportDataWrapperData(Map<ReportKey, ReportData> map, List<ReportDataWrapper> data) {
        for (Map.Entry<ReportKey, ReportData> entry : map.entrySet()) {
            TT079ReportDataWrapper reportDataWrapper = (TT079ReportDataWrapper) createReportDataWrapper();

            reportDataWrapper.setPeriod(entry.getKey().getDate());
            reportDataWrapper.setProductionMode(entry.getKey().getProductionMode());
            reportDataWrapper.setSku(entry.getKey().getSku());
            reportDataWrapper.setRunningTime(entry.getValue().getRunningTime());
            reportDataWrapper.setProductNumber(entry.getValue().getGood(), entry.getValue().getBad());

            TT079ReportData tt079Entry = (TT079ReportData) entry.getValue();

            reportDataWrapper.setStatisticsData(tt079Entry.getGood(), tt079Entry.getBad(), tt079Entry.getInkDetected());
            reportDataWrapper.setBlobRate(tt079Entry.getGood(), tt079Entry.getBad(), tt079Entry.getInkDetected());

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
        
        if (hasPermission(TT079Permission.PRODUCTION_REPORT_BLOB_RATE)) {
        	table.getColumnModel().getColumn(8).setPreferredWidth(30);
        }
    }
}