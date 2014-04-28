package com.sicpa.standard.sasscl.remoteControl.systemEvent;

import java.util.Date;
import java.util.List;

import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.sasscl.monitoring.mbean.sas.RemoteControlSasMBean;
import com.sicpa.standard.sasscl.monitoring.system.event.BasicSystemEvent;
import com.sicpa.standard.sasscl.remoteControl.BackgroundTask;
import com.sicpa.standard.sasscl.view.monitoring.SystemEventPanel;

public class RemoteSystemEventPanel extends SystemEventPanel {

	private static final long serialVersionUID = 1L;

	protected RemoteControlSasMBean remoteBean;

	public RemoteSystemEventPanel() {

	}

	@Override
	protected List<BasicSystemEvent> getSystemEvents(final Date from, final Date to) {
		return this.remoteBean.getSystemEventList(from, to);
	}

	public void setRemoteBean(final RemoteControlSasMBean remoteBean) {
		this.remoteBean = remoteBean;
	}

	@Override
	protected void requestAndHandleEvents(final Date from, final Date to) {
		new BackgroundTask(new Runnable() {
			@Override
			public void run() {
				final List<BasicSystemEvent> list = getSystemEvents(from, to);
				ThreadUtils.invokeLater(new Runnable() {
					@Override
					public void run() {
						getTable().addRow(list);
					}
				});
			}
		}).start();
	}
}
