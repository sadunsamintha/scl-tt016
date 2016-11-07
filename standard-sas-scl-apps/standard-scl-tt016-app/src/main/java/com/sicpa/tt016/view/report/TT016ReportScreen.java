package com.sicpa.tt016.view.report;

import com.sicpa.standard.sasscl.view.report.ReportScreen;
import com.sicpa.standard.sasscl.view.report.ReportTable;


public class TT016ReportScreen extends ReportScreen {

    @Override
    public ReportTable getTable() {
        if (table == null) {
            table = new TT016ReportTable();
        }

        return table;
    }
}
