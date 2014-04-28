package com.sicpa.standard.sasscl.devices.remote.simulator;

import com.sicpa.standard.sasscl.devices.remote.RemoteServerException;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.sicpadata.reader.IAuthenticator;

public class RemoteServerSimulatorNoCoding extends RemoteServerSimulator {

	public RemoteServerSimulatorNoCoding(RemoteServerSimulatorModel model) throws RemoteServerException {
		super(model);
	}

	public RemoteServerSimulatorNoCoding(String configFile) throws RemoteServerException {
		super(configFile);
	}

	@Override
	public IAuthenticator getAuthenticator() throws RemoteServerException {
		AcceptAllAuthenticator auth = new AcceptAllAuthenticator();
		auth.setProductionParameters(productionParameters);
		return auth;
	}

	@Override
	public void downloadEncoder(int batchesQuantity, CodeType codeType, int year) throws RemoteServerException {
	}
}
