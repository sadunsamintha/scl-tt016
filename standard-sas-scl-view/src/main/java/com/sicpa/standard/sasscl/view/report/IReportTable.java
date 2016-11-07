package com.sicpa.standard.sasscl.view.report;

import javax.swing.*;
import java.util.Map;

public interface IReportTable {

    void setData(final Map<ReportKey, ReportData> map, final boolean groupByProduct, final boolean detailed);

    void printReport();

    JComponent getComponent();
}
