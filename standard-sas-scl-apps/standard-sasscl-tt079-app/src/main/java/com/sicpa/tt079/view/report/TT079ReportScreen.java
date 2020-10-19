package com.sicpa.tt079.view.report;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.sasscl.event.UserLoginEvent;
import com.sicpa.standard.sasscl.event.UserLogoutEvent;
import com.sicpa.standard.sasscl.view.report.IReportTable;
import com.sicpa.standard.sasscl.view.report.ReportScreen;


public class TT079ReportScreen extends ReportScreen {
	
    @Override
    public IReportTable getTable() {
        if (table == null) {
            table = new TT079ReportTable();
        }

        return table;
    }
    
    @Subscribe
	public void handleUserLogin(UserLoginEvent evt) {
    	removeAll();
		this.busyPanel = null;
		this.mainPanel = null;
		this.buttonDay = null;
		this.buttonWeek = null;
		this.buttonMonth = null;
		this.buttonGroupByProduct = null;
		this.buttonDailyDetailed = null;
		this.buttonPrint = null;
		this.dateFrom = null;
		this.dateTo   = null;
		getMainPanel();
		initGUI();
		revalidate();
		getTable().getComponent().setVisible(false);
	}

	@Subscribe
	public void handleUserLogout(UserLogoutEvent evt) {
		removeAll();
		this.busyPanel = null;
		this.mainPanel = null;
		this.buttonDay = null;
		this.buttonWeek = null;
		this.buttonMonth = null;
		this.buttonGroupByProduct = null;
		this.buttonDailyDetailed = null;
		this.buttonPrint = null;
		this.dateFrom = null;
		this.dateTo   = null;
		getMainPanel();
		initGUI();
		revalidate();
		getTable().getComponent().setVisible(false);
	}
}