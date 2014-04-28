package com.sicpa.standard.sasscl.remoteControl.productionStatistics;

import java.awt.Component;

import com.sicpa.standard.client.common.view.IGUIComponentGetter;
import com.sicpa.standard.sasscl.monitoring.mbean.sas.RemoteControlSasMBean;

public class RemoteProductionStatisticsPanelGetter implements IGUIComponentGetter {

	protected RemoteProductionStatisticsPanel panel;
	protected RemoteControlSasMBean controlBean;

	@Override
	public Component getComponent() {
		if (this.panel == null) {
			this.panel = new RemoteProductionStatisticsPanel(this.controlBean);
		}
		return this.panel;
	}

	public void setControlBean(final RemoteControlSasMBean controlBean) {
		this.controlBean = controlBean;
	}
}
