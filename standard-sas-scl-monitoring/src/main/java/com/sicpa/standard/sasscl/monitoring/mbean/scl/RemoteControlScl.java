package com.sicpa.standard.sasscl.monitoring.mbean.scl;

import com.sicpa.standard.sasscl.monitoring.mbean.sas.RemoteControlSas;

public class RemoteControlScl extends RemoteControlSas implements RemoteControlSclMBean {

	@Override
	public SclAppMBean getAppBean() {
		return (SclAppMBean) super.getAppBean();
	}

	public void setAppBean(SclAppMBean sasApp) {
		this.appBean = sasApp;
	}

	@Override
	public String getEncoderID() {
		return getAppBean().getEncoderID();
	}

	@Override
	public String getInkLevel() {
		return getAppBean().getInkLevel();
	}

	@Override
	public String getMakeupLevel() {
		return getAppBean().getMakeupLevel();
	}

	@Override
	public String getDevicePrinterStatus() {
		return getAppBean().getDevicePrinterStatus();
	}
}
