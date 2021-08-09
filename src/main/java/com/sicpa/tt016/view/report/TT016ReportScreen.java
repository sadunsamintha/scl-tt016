package com.sicpa.tt016.view.report;

import com.sicpa.standard.sasscl.view.report.IReportTable;
import com.sicpa.standard.sasscl.view.report.ReportScreen;


public class TT016ReportScreen extends ReportScreen {

    @Override
    public IReportTable getTable() {
        if (table == null) {
            table = new TT016ReportTable();
        }

        return table;
    }
}