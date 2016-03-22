package com.sicpa.tt018.scl.remoteServer.simu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.devices.remote.RemoteServerException;
import com.sicpa.standard.sasscl.devices.remote.simulator.RemoteServerSimulator;
import com.sicpa.standard.sasscl.devices.remote.simulator.RemoteServerSimulatorModel;
import com.sicpa.standard.sasscl.sicpadata.generator.IEncoder;
import com.sicpa.standard.sasscl.sicpadata.reader.IAuthenticator;
import com.sicpa.standard.sicpadata.api.exception.SicpadataException;
import com.sicpa.standard.sicpadata.api.exception.UnknownModeException;
import com.sicpa.standard.sicpadata.api.exception.UnknownSystemTypeException;
import com.sicpa.standard.sicpadata.api.exception.UnknownVersionException;
import com.sicpa.standard.sicpadata.spi.password.NoSuchPasswordException;
import com.sicpa.tt018.interfaces.scl.master.dto.AlbaniaEncoderDTO;
import com.sicpa.tt018.interfaces.security.IAlbaniaAuthenticator;
import com.sicpa.tt018.scl.model.authenticator.AlbaniaAuthenticatorWrapper;
import com.sicpa.tt018.scl.remoteServer.utilities.AlbaniaRemoteServerUtilities;

public class AlbaniaRemoteServerSimulator extends RemoteServerSimulator {

	public AlbaniaRemoteServerSimulator(RemoteServerSimulatorModel model) {
		super(model);
	}

	private static final Logger logger = LoggerFactory.getLogger(AlbaniaRemoteServerSimulator.class);
	private static int SIMU_SUB_SYSTEM_ID = -1;

	private AlbaniaRemoteServerModelSimulator remoteServerSimulatorModel;
	private static final String SIMU_CRYPTO_PWD = "PWD";

	@Override
	public IAuthenticator getAuthenticator() throws RemoteServerException {
		logger.debug("Retrieveing authenticator from master....");

		IAlbaniaAuthenticator albaniaAuthenticator = remoteServerSimulatorModel.loadSimuAuthenticator();
		fireDeviceStatusChanged(DeviceStatus.CONNECTED);
		return new AlbaniaAuthenticatorWrapper(albaniaAuthenticator, SIMU_CRYPTO_PWD, cryptoFieldsConfig);

	}

	@Override
	public synchronized void setupBusinessCrypto() {
	}

	@Override
	public IEncoder createOneEncoder(int year, int codeTypeId) throws UnknownModeException, UnknownVersionException,
			UnknownSystemTypeException, SicpadataException, NoSuchPasswordException {

		AlbaniaEncoderDTO encoder = remoteServerSimulatorModel.provideEncoders(1, codeTypeId, SIMU_SUB_SYSTEM_ID)
				.get(0);

		return AlbaniaRemoteServerUtilities.wrapEncoder(encoder, year, SIMU_SUB_SYSTEM_ID, getCryptoFieldsConfig(),
				SIMU_CRYPTO_PWD, codeTypeId);

	}
}
