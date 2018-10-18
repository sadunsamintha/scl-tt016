package com.sicpa.tt016.scl.remote;

import com.sicpa.standard.sasscl.devices.remote.connector.AbstractMasterConnector;
import com.sicpa.standard.sasscl.devices.remote.connector.IConnectable;
import com.sicpa.tt016.scl.remote.remoteservices.ITT016RemoteServicesMSASLegacy;

public class TT016MasterConnectorSAS extends AbstractMasterConnector implements IConnectable {

	private ITT016RemoteServicesMSASLegacy remoteServices;

	public void setRemoteServices(ITT016RemoteServicesMSASLegacy remoteServices) {
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
