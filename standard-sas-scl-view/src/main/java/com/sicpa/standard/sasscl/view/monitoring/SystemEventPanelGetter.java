package com.sicpa.standard.sasscl.view.monitoring;

import javax.swing.JPanel;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.view.SecuredComponentGetter;
import com.sicpa.standard.sasscl.security.SasSclPermission;

public class SystemEventPanelGetter extends SecuredComponentGetter {

	protected SystemEventPanel panel;

	protected SystemEventPanelGetter() {
		super(SasSclPermission.MONITORING_SYSTEM_EVENT, "label.monitoring.system.event");
	}

	@Override
	public JPanel getComponent() {
		if (panel == null) {
			panel = new SystemEventPanel();
			EventBusService.register(panel);
		}
		return panel;
	}
}
