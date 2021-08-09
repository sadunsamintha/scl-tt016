package com.sicpa.tt016.scl.remote;

import com.sicpa.standard.sasscl.devices.remote.connector.AbstractMasterConnector;
import com.sicpa.standard.sasscl.devices.remote.connector.IConnectable;
import com.sicpa.tt016.scl.remote.remoteservices.ITT016RemoteServices;

public class TT016MasterConnector extends AbstractMasterConnector implements IConnectable {

	private ITT016RemoteServices remoteServices;

	public void setRemoteServices(ITT016RemoteServices remoteServices) {
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
