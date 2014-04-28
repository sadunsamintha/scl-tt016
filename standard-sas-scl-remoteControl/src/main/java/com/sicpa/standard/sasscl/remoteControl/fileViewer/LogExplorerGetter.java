package com.sicpa.standard.sasscl.remoteControl.fileViewer;

import java.awt.Component;

import com.sicpa.standard.client.common.view.IGUIComponentGetter;
import com.sicpa.standard.sasscl.monitoring.mbean.sas.RemoteControlSasMBean;

public class LogExplorerGetter implements IGUIComponentGetter {

	protected RemoteControlSasMBean controlBean;
	protected FileViewer viewer;

	@Override
	public Component getComponent() {
		if (this.viewer == null) {
			this.viewer = new FileViewer("log");
			this.viewer.setControlBean(this.controlBean);
		}
		return this.viewer;
	}

	public void setControlBean(final RemoteControlSasMBean controlBean) {
		this.controlBean = controlBean;
	}

}
