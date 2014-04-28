package com.sicpa.standard.sasscl.view.monitoring;

import javax.swing.JPanel;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.view.SecuredComponentGetter;
import com.sicpa.standard.sasscl.security.SasSclPermission;

public class ProductionStatisticsPanelGetter extends SecuredComponentGetter {

	protected ProductionStatisticsPanelGetter() {
		super(SasSclPermission.MONITORING_PRODUCTION_STATISTICS, "label.monitoring.production.statistics");
	}

	protected ProductionStatisticsPanel panel;

	@Override
	public JPanel getComponent() {
		if (panel == null) {
			panel = new ProductionStatisticsPanel();
			EventBusService.register(panel);
		}
		return panel;
	}
}
