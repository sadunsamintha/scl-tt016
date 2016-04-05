package com.sicpa.tt016.scl.remote;

import com.sicpa.standard.sasscl.devices.remote.connector.AbstractMasterConnector;
import com.sicpa.standard.sasscl.devices.remote.connector.IConnectable;
import com.sicpa.tt016.scl.remote.remoteservices.IRemoteServices;

public class MasterConnector extends AbstractMasterConnector implements IConnectable {

	private IRemoteServices remoteServices;

	public void setRemoteServices(IRemoteServices remoteServices) {
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
