package com.sicpa.tt077.view.report;

import com.sicpa.standard.sasscl.view.report.IReportTable;
import com.sicpa.standard.sasscl.view.report.ReportScreen;

public class TT077ReportScreen extends ReportScreen {

    @Override
    public IReportTable getTable() {
        if (table == null) {
            table = new TT077ReportTable();
        }

        return table;
    }
}
