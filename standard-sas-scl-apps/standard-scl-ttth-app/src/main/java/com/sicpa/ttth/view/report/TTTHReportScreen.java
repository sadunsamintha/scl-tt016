package com.sicpa.ttth.view.report;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import org.jdesktop.swingx.JXDatePicker;

import com.sicpa.standard.sasscl.view.report.IReportTable;
import com.sicpa.standard.sasscl.view.report.ReportScreen;


public class TTTHReportScreen extends ReportScreen {

    private static final long serialVersionUID = 1L;
    private static final long unixEpochInitTime = 1970;

    private Date selectedDate;

    @Override
    public IReportTable getTable() {
        if (table == null) {
            table = new TTTHReportTable();
        }

        return table;
    }

    @Override
    public JXDatePicker getDateFrom() {
        if (this.dateFrom == null) {
            this.dateFrom = new JXDatePicker();
            DateFormat dateFormat = new SimpleDateFormat("dd/MM");
            this.dateFrom.setFormats(dateFormat);
            this.dateFrom.setDate(new Date());

            this.dateFrom.addPropertyChangeListener("date", evt -> {
                LocalDate localDate = dateFrom
                    .getDate()
                    .toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate();
                //Handles the double call issue for JXDatePicker
                if (localDate.getYear() != unixEpochInitTime) {
                    selectedDate = dateFrom.getDate();
                    if (selectedDate != null) {
                        dateTo.getMonthView().setLowerBound(selectedDate);
                        dateTo.setDate(selectedDate);
                    }
                } else {
                    dateFrom.setDate(selectedDate);
                }
            });
        }
        return this.dateFrom;
    }

    @Override
    public JXDatePicker getDateTo() {
        if (this.dateTo == null) {
            this.dateTo = new JXDatePicker();
            DateFormat dateFormat = new SimpleDateFormat("dd/MM");
            this.dateTo.setFormats(dateFormat);
            this.dateTo.setDate(new Date());
            this.dateTo.getMonthView().setLowerBound(new Date());
        }
        return this.dateTo;
    }
}