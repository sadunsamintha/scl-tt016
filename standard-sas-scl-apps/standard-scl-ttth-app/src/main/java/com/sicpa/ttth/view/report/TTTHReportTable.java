package com.sicpa.ttth.view.report;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.sasscl.view.report.ReportData;
import com.sicpa.standard.sasscl.view.report.ReportDataWrapper;
import com.sicpa.standard.sasscl.view.report.ReportKey;
import com.sicpa.standard.sasscl.view.report.ReportTable;

import static com.sicpa.ttth.scl.utils.TTTHCalendarUtils.TH_YEAR_DIFF;

public class TTTHReportTable extends ReportTable {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(TTTHReportTable.class);

    @Override
    protected void setReportDataWrapperData(Map<ReportKey, ReportData> map, List<ReportDataWrapper> data) {
        for (Map.Entry<ReportKey, ReportData> entry : map.entrySet()) {
            ReportDataWrapper reportDataWrapper = createReportDataWrapper();

            try {
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yy");
                SimpleDateFormat sdfTH = new SimpleDateFormat("dd/MM/yyyy");
                cal.setTime(sdf.parse(entry.getKey().getDate()));
                cal.add(Calendar.YEAR, TH_YEAR_DIFF);
                reportDataWrapper.setPeriod(sdfTH.format(cal.getTime()));
            } catch (ParseException e) {
                logger.error("Failed to convert to Buddhist Years. Falling back to Georgian", e);
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