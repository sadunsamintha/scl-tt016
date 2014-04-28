package com.sicpa.standard.sasscl.remoteControl.threadDump;

import java.awt.Component;

import com.sicpa.standard.client.common.view.IGUIComponentGetter;
import com.sicpa.standard.sasscl.monitoring.mbean.sas.RemoteControlSasMBean;

public class ThreadDumpPanelGetter implements IGUIComponentGetter {

	protected RemoteControlSasMBean controlBean;
	protected ThreadDumpPanel threadDumpPanel;

	@Override
	public Component getComponent() {
		if (this.threadDumpPanel == null) {
			this.threadDumpPanel = new ThreadDumpPanel();
			this.threadDumpPanel.setControlBean(this.controlBean);
		}
		return this.threadDumpPanel;
	}

	public void setControlBean(final RemoteControlSasMBean controlBean) {
		this.controlBean = controlBean;
	}

}
