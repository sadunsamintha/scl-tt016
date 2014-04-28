package com.sicpa.standard.sasscl.view.report;

import javax.swing.JPanel;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.view.SecuredComponentGetter;
import com.sicpa.standard.sasscl.security.SasSclPermission;

public class ReportScreenGetter extends SecuredComponentGetter {

	public ReportScreenGetter() {
		super(SasSclPermission.PRODUCTION_REPORT, "report.production");
	}

	protected ReportScreen screen;

	@Override
	public JPanel getComponent() {
		if (screen == null) {
			screen = new ReportScreen();
			EventBusService.register(screen);
		}
		return this.screen;
	}

}
