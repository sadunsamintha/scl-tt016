package com.sicpa.standard.sasscl.view.main.systemInfo;

import com.sicpa.standard.client.common.view.mvc.AbstractObservableModel;

public class SystemInfoModel extends AbstractObservableModel {

	protected String appVersion;
	protected boolean remoteServerConnected;
	protected String plcVersion;

	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	public void setPlcVersion(String plcVersion) {
		this.plcVersion = plcVersion;
	}

	public String getPlcVersion() {
		return plcVersion;
	}

	public boolean isRemoteServerConnected() {
		return remoteServerConnected;
	}

	public void setRemoteServerConnected(boolean remoteServerConnected) {
		this.remoteServerConnected = remoteServerConnected;
	}
}
