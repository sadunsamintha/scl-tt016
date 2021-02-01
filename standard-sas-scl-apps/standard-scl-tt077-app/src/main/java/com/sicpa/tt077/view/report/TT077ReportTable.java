package com.sicpa.tt077.view.report;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.sasscl.view.report.ReportData;
import com.sicpa.standard.sasscl.view.report.ReportDataWrapper;
import com.sicpa.standard.sasscl.view.report.ReportKey;
import com.sicpa.standard.sasscl.view.report.ReportTable;
import com.sicpa.tt077.scl.utils.TT077CalendarUtils;

public class TT077ReportTable extends ReportTable {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(TT077ReportTable.class);

    @Override
    protected void setReportDataWrapperData(Map<ReportKey, ReportData> map, List<ReportDataWrapper> data) {
        for (Map.Entry<ReportKey, ReportData> entry : map.entrySet()) {
            ReportDataWrapper reportDataWrapper = createReportDataWrapper();
            try {
                String dateStr = entry.getKey().getDate();
                if (dateStr.length() > 40) {
                    //Date string contains two date.
                    String[] dateStrArr = dateStr.split(" - ");
                    for (int i = 0; i < dateStrArr.length; i++) {
                        dateStrArr[i] = TT077CalendarUtils.convertToTZCal(dateStrArr[i]);
                    }
                    reportDataWrapper.setPeriod(dateStrArr[0] + " - " + dateStrArr[1]);
                } else {
                    reportDataWrapper.setPeriod(entry.getKey().getDate());
                }
            } catch (ParseException e) {
                logger.error("Failed to convert to TZ calendar. Falling back to UTC", e);
                reportDataWrapper.setPeriod(entry.getKey().getDate());
            }
            reportDataWrapper.setProductionMode(entry.getKey().getProductionMode());
            reportDataWrapper.setSku(entry.getKey().getSku());
            reportDataWrapper.setProductNumber(entry.getValue().getGood(), entry.getValue().getBad());
            reportDataWrapper.setRunningTime(entry.getValue().getRunningTime());

            data.add(reportDataWrapper);
        }
    }
}
