package com.sicpa.ttth.view.report;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.apache.commons.lang.time.DateUtils;
import org.jdesktop.swingx.JXDatePicker;

import com.sicpa.standard.sasscl.view.monitoring.IProductionStatisticsAggregator;
import com.sicpa.standard.sasscl.view.report.IReportTable;
import com.sicpa.standard.sasscl.view.report.ReportScreen;


public class TTTHReportScreen extends ReportScreen {

    private static final int TH_YEAR_DIFF = 543;

    @Override
    public IReportTable getTable() {
        if (table == null) {
            table = new TTTHReportTable();
        }

        return table;
    }

    @Override
    protected void getAndShowReport() {
        IProductionStatisticsAggregator aggre = productionStatisticsAggregatorFactory.getInstance();

        Date from = DateUtils.round(convertBackToGeorgian(getDateFrom().getDate()), Calendar.DAY_OF_MONTH);
        Date to = DateUtils.ceiling(convertBackToGeorgian(getDateTo().getDate()), Calendar.DAY_OF_MONTH);// go to next day
        to = new Date(to.getTime() - 1);// remove 1 ms to come back

        aggre.aggregate(getProductionStatistics(from, to), getSelectedPeriod(), getButtonGroupByProduct().isSelected(),
            getButtonDailyDetailed().isSelected(), from, to);
        getTable().setData(aggre.getMapData(), getButtonGroupByProduct().isSelected(),
            getButtonDailyDetailed().isSelected());
    }

    @Override
    public JXDatePicker getDateFrom() {
        if (this.dateFrom == null) {
            this.dateFrom = new JXDatePicker();
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            this.dateFrom.setFormats(dateFormat);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.YEAR, TH_YEAR_DIFF);
            this.dateFrom.setDate(calendar.getTime());
        }
        return this.dateFrom;
    }

    @Override
    public JXDatePicker getDateTo() {
        if (this.dateTo == null) {
            this.dateTo = new JXDatePicker();
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            this.dateTo.setFormats(dateFormat);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.YEAR, TH_YEAR_DIFF);
            this.dateTo.setDate(calendar.getTime());
        }
        return this.dateTo;
    }

    private Date convertBackToGeorgian(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, -TH_YEAR_DIFF);
        return calendar.getTime();
    }

}