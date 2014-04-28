package com.sicpa.standard.sasscl.monitoring.mbean.scl;

import com.sicpa.standard.sasscl.monitoring.mbean.sas.SasAppBeanStatistics;
import com.sicpa.standard.sasscl.monitoring.system.AbstractSystemEvent;
import com.sicpa.standard.sasscl.monitoring.system.SystemEventType;

public class SclAppBeanStatistics extends SasAppBeanStatistics {

	private static final long serialVersionUID = 1L;

	protected String encoderId;

	protected String printerInkLevel;
	protected String printerMakeUpLevel;

	public String getEncoderId() {
		return this.encoderId == null ? "" : this.encoderId;
	}

	@Override
	public void handleSystemEvent(final AbstractSystemEvent event) {
		super.handleSystemEvent(event);
		if (event.getType().equals(SystemEventType.GET_CODE_FROM_ENCODER)) {
			setEncoderId(event.getMessage() + "");
		} else if (event.getType().equals(SystemEventType.PRINTER_INK_LEVEL)) {
			setPrinterInkLevel(event.getMessage() + "");
		} else if (event.getType().equals(SystemEventType.PRINTER_MAKEUP_LEVEL)) {
			setPrinterMakeUpLevel(event.getMessage() + "");
		}
	}

	public void setEncoderId(final String encoderId) {
		this.encoderId = encoderId;
	}

	public void setPrinterInkLevel(final String printerInkLevel) {
		this.printerInkLevel = printerInkLevel;
	}

	public void setPrinterMakeUpLevel(final String printerMakeUpLevel) {
		this.printerMakeUpLevel = printerMakeUpLevel;
	}

	public String getPrinterInkLevel() {
		return this.printerInkLevel == null ? "" : this.printerInkLevel;
	}

	public String getPrinterMakeUpLevel() {
		return this.printerMakeUpLevel == null ? "" : this.printerMakeUpLevel;
	}
}
