package com.sicpa.standard.sasscl.monitoring.mbean.scl;

import java.util.ArrayList;
import java.util.Collection;

import com.sicpa.standard.sasscl.controller.productionconfig.IProductionConfig;
import com.sicpa.standard.sasscl.controller.productionconfig.config.PrinterConfig;
import com.sicpa.standard.sasscl.monitoring.mbean.sas.SasApp;
import com.sicpa.standard.sasscl.monitoring.mbean.sas.SasAppBeanStatistics;

public class SclApp extends SasApp implements SclAppMBean {

	@Override
	public String getEncoderID() {
		return ((SclAppBeanStatistics) stats).getEncoderId();
	}

	public void setStats(SasAppBeanStatistics stats) {
		this.stats = stats;
	}

	@Override
	public String getInkLevel() {
		return ((SclAppBeanStatistics) stats).getPrinterInkLevel();
	}

	@Override
	public String getMakeupLevel() {
		return ((SclAppBeanStatistics) stats).getPrinterMakeUpLevel();
	}

	@Override
	public String getDevicePrinterStatus() {
		IProductionConfig pc = productionConfigProvider.get();
		if (pc == null || pc.getPrinterConfigs() == null) {
			return "";
		}
		Collection<String> printers = new ArrayList<String>();
		for (PrinterConfig cc : pc.getPrinterConfigs()) {
			printers.add(cc.getId());
		}
		return getDeviceStatus(printers);
	}

}
