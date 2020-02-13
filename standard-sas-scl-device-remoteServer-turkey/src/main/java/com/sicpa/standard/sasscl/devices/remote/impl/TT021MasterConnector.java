package com.sicpa.standard.sasscl.devices.remote.impl;

import com.sicpa.standard.sasscl.devices.remote.connector.AbstractMasterConnector;
import com.sicpa.standard.sasscl.devices.remote.connector.IConnectable;
import com.sicpa.standard.sasscl.devices.remote.impl.remoteservices.ITT021RemoteServices;

public class TT021MasterConnector extends AbstractMasterConnector implements IConnectable {

	private ITT021RemoteServices remoteServices;

	public void setRemoteServices(ITT021RemoteServices remoteServices) {
		this.remoteServices = remoteServices;
	}

	@Override
	protected void login() throws Exception {
		remoteServices.login();
	}

	@Override
	protected boolean checkIsAlive() {
		return remoteServices.isAlive();
	}
}
