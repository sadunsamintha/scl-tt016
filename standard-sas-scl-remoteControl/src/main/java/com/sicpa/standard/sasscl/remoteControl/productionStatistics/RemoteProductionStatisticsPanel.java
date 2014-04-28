package com.sicpa.standard.sasscl.remoteControl.productionStatistics;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.sasscl.monitoring.mbean.sas.RemoteControlSasMBean;
import com.sicpa.standard.sasscl.monitoring.statistics.incremental.IncrementalStatistics;
import com.sicpa.standard.sasscl.monitoring.statistics.production.ProductionStatistics;
import com.sicpa.standard.sasscl.remoteControl.BackgroundTask;
import com.sicpa.standard.sasscl.view.monitoring.ProductionStatisticsPanel;

public class RemoteProductionStatisticsPanel extends ProductionStatisticsPanel {

	private static final long serialVersionUID = 1L;

	protected RemoteControlSasMBean remoteBean;

	public RemoteProductionStatisticsPanel(final RemoteControlSasMBean remoteBean) {
		this.remoteBean = remoteBean;
	}

	@Override
	protected List<ProductionStatistics> getProductionStatistics(final Date start, final Date stop) {
		return this.remoteBean.getProductionStatistics(start, stop);
	}

	@Override
	protected List<IncrementalStatistics> getIncrementalStatistics(final Date start, final Date stop) {
		return this.remoteBean.getIncrementalStatistics(start, stop);
	}

	public void setRemoteBean(final RemoteControlSasMBean remoteBean) {
		this.remoteBean = remoteBean;
	}

	@Override
	protected void queryAndShowChart(final Date start, final Date stop) {
		new BackgroundTask(new Runnable() {
			@Override
			public void run() {
				List<IncrementalStatistics> list = getIncrementalStatistics(start, stop);
				createAndshowChart(list);
			}
		}).start();
	}

	@Override
	protected void requestAndHandleEvents(final Date from, final Date to) {
		new BackgroundTask(new Runnable() {
			@Override
			public void run() {
				final Collection<ProductionStatistics> list = getProductionStatistics(from, to);
				if (list != null) {
					ThreadUtils.invokeLater(new Runnable() {
						@Override
						public void run() {
							getTable().addRow(list);
						}
					});
				}
			}
		}).start();
	}
}
