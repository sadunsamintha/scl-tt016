package com.sicpa.standard.sasscl.remoteControl.productionReport;

import java.util.Date;
import java.util.List;

import com.sicpa.standard.sasscl.monitoring.mbean.sas.RemoteControlSasMBean;
import com.sicpa.standard.sasscl.monitoring.statistics.production.ProductionStatistics;
import com.sicpa.standard.sasscl.remoteControl.BackgroundTask;
import com.sicpa.standard.sasscl.view.report.ReportScreen;

public class RemoteReportScreen extends ReportScreen {

	private static final long serialVersionUID = 1L;

	protected RemoteControlSasMBean controlBean;

	public RemoteReportScreen(final RemoteControlSasMBean controlBean) {
		super();
		this.controlBean = controlBean;
	}

	@Override
	protected List<ProductionStatistics> getProductionStatistics(final Date from, final Date to) {
		return this.controlBean.getProductionStatistics(from, to);
	}

	@Override
	protected void getAndShowReport() {
		
		Runnable superTask=new  Runnable() {
			public void run() {
				RemoteReportScreen.super.getAndShowReport();
			}
		};
		new BackgroundTask(superTask).start();
	}

}
