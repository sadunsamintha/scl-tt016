package com.sicpa.ttth.view.report;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.sasscl.view.report.ReportData;
import com.sicpa.standard.sasscl.view.report.ReportDataWrapper;
import com.sicpa.standard.sasscl.view.report.ReportKey;
import com.sicpa.standard.sasscl.view.report.ReportTable;
import com.sicpa.ttth.scl.utils.TTTHCalendarUtils;

public class TTTHReportTable extends ReportTable {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(TTTHReportTable.class);

    @Override
    protected String[] getFieldsGroupByProduct() {
        return new String[] { "period", "productionMode", "sku", "runningTime", "total", "good", "bad", "batchJobId"};
    }

    @Override
    protected String[] getTitleGroupByProduct() {
        return new String[] { Messages.get("production.report.period"),
            Messages.get("production.report.productionMode"), Messages.get("production.report.sku"),
            Messages.get("production.report.runningTime"), Messages.get("production.report.total"),
            Messages.get("production.report.good"), Messages.get("production.report.bad"),
            Messages.get("production.report.batch.job")
        };
    }

    @Override
    protected void setReportDataWrapperData(Map<ReportKey, ReportData> map, List<ReportDataWrapper> data) {
        for (Map.Entry<ReportKey, ReportData> entry : map.entrySet()) {
            TTTHReportDataWrapper reportDataWrapper = (TTTHReportDataWrapper) createReportDataWrapper();
            try {
                String dateStr = entry.getKey().getDate();
                if (dateStr.length() > 40) {
                    //Date string contains multiple date.
                    //The sub stringing is safe due to preset format.
                    String first =
                        TTTHCalendarUtils.convertToBuddhistCal(dateStr.substring(0, 19)
                        ,true);
                    String second =
                        TTTHCalendarUtils.convertToBuddhistCal(dateStr.substring(22, 41)
                        ,true);
                    reportDataWrapper.setPeriod(first + " - " + second);

                } else {
                    reportDataWrapper.setPeriod(TTTHCalendarUtils
                        .convertToBuddhistCal(entry.getKey().getDate(), false));
                }
            } catch (ParseException e) {
                logger.error("Failed to convert to Buddhist Years. Falling back to Georgian", e);
                reportDataWrapper.setPeriod(entry.getKey().getDate());
            }

            reportDataWrapper.setProductionMode(entry.getKey().getProductionMode());
            reportDataWrapper.setSku(entry.getKey().getSku());
            reportDataWrapper.setProductNumber(entry.getValue().getGood(), entry.getValue().getBad());
            reportDataWrapper.setRunningTime(entry.getValue().getRunningTime());

            TTTHReportData ttthEntry = (TTTHReportData) entry.getValue();

            reportDataWrapper.setBatchJobId(ttthEntry.getBatchJobId());

            data.add(reportDataWrapper);
        }
    }

    @Override
    protected ReportDataWrapper createReportDataWrapper() {
        return new TTTHReportDataWrapper();
    }

}