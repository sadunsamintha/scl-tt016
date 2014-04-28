package com.sicpa.standard.sasscl.skucheck.view;

import java.awt.Component;

import com.sicpa.standard.client.common.security.Permission;
import com.sicpa.standard.client.common.view.SecuredComponentGetter;
import com.sicpa.standard.sasscl.skucheck.SkuCheckFacade;

public class StatisticsPanelGetter extends SecuredComponentGetter {

	public static final Permission SEE_STATISTICS = new Permission("SKU_CHECK_AQUISTION_STAT_PANEL");

	protected StatisticsPanel panel;
	protected int devicesCount;
	protected SkuCheckFacade facade;

	public StatisticsPanelGetter() {
		super(SEE_STATISTICS, "sku.check.acquisition.statistics");
	}

	@Override
	public Component getComponent() {
		if (panel == null) {
			panel = new StatisticsPanel(devicesCount);
			panel.setFacade(facade);
		}
		return panel;
	}

	public void setFacade(SkuCheckFacade facade) {
		this.facade = facade;
		if (panel != null) {
			panel.setFacade(facade);
		}
	}

	public void setDevicesCount(int devicesCount) {
		this.devicesCount = devicesCount;
	}
}
