package com.sicpa.standard.sasscl.remoteControl.log;

import java.awt.Component;

import com.sicpa.standard.client.common.view.IGUIComponentGetter;
import com.sicpa.standard.sasscl.monitoring.mbean.sas.RemoteControlSasMBean;

public class RemoteLogLevelPanelGetter implements IGUIComponentGetter {

	protected RemoteControlSasMBean controlBean;
	protected RemoteLogLevelPanel panel;

	public void setControlBean(final RemoteControlSasMBean controlBean) {
		this.controlBean = controlBean;
	}

	@Override
	public Component getComponent() {
		if (this.panel == null) {
			this.panel = new RemoteLogLevelPanel(this.controlBean);
		}
		return this.panel;
	}

}
