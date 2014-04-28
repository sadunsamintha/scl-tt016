package com.sicpa.standard.sasscl.devices.remote.simulator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.sicpa.standard.sasscl.devices.remote.RemoteServerException;
import com.sicpa.standard.sasscl.devices.remote.stdCrypto.StdCryptoEncoderWrapper;
import com.sicpa.standard.sasscl.devices.remote.stdCrypto.StdCryptoEncoderWrapperSimulator;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.sicpadata.CryptographyException;
import com.sicpa.standard.sasscl.sicpadata.generator.IEncoder;
import com.sicpa.standard.sasscl.sicpadata.generator.impl.CodeListEncoder;
import com.sicpa.standard.sicpadata.api.exception.SicpadataException;
import com.sicpa.standard.sicpadata.api.exception.UnknownModeException;
import com.sicpa.standard.sicpadata.api.exception.UnknownSystemTypeException;
import com.sicpa.standard.sicpadata.api.exception.UnknownVersionException;
import com.sicpa.standard.sicpadata.spi.password.NoSuchPasswordException;

public class RemoteServerSimulatorThatProvidesCodeList extends RemoteServerSimulator {

	public RemoteServerSimulatorThatProvidesCodeList(final RemoteServerSimulatorModel model)
			throws RemoteServerException {
		super(model);
	}

	public RemoteServerSimulatorThatProvidesCodeList(final String configFile) throws RemoteServerException {
		super(configFile);
	}

	/**
	 * create a new encoder
	 */
	protected StdCryptoEncoderWrapper getNewEncoder(final int batchId, final int year, final CodeType codeType)
			throws UnknownModeException, UnknownVersionException, UnknownSystemTypeException, SicpadataException,
			NoSuchPasswordException {
		return new StdCryptoEncoderWrapperSimulator(batchId,new Random().nextInt(), this.generateEncoders(batchId,
				codeType.getId()), year, this.config.getSubsystemId(), 999999, this.cryptoFieldsConfig,
				(int) codeType.getId());
	}

	@Override
	@Deprecated
	public IEncoder createOneEncoder(int year, int codeTypeId) {
		throw new IllegalAccessError();
	}

	@Override
	public void downloadEncoder(final int quantity, final CodeType codeType, final int year)
			throws RemoteServerException {

		if (simulatorModel == null) {
			throw new RemoteServerException("Remote server model is not set");
		}

		if (quantity == 0) {
			return;
		}

		long numberOfCodesPerBatch = simulatorModel.getRequestNumberOfCodes();

		if (numberOfCodesPerBatch == 0) {
			return;
		}

		for (IEncoder encoder : createEncoders(quantity, codeType, year)) {
			storeEncoder(encoder, year);
		}
	}

	public List<IEncoder> createEncoders(final int quantity, final CodeType codeType, final int year)
			throws RemoteServerException {
		long numberOfCodesPerBatch = simulatorModel.getRequestNumberOfCodes();

		List<String> codeList = null;
		IEncoder encoder = null;
		List<IEncoder> encoders = new ArrayList<IEncoder>();

		int batchId = 0;

		for (int i = 0; i < quantity; i++) {

			codeList = new ArrayList<String>();

			if (encoder == null) {
				try {
					encoder = this.getNewEncoder(batchId, year, codeType);
				} catch (Exception e) {
					throw new RemoteServerException(e);
				}
				batchId++;
			}

			try {
				codeList.addAll(encoder.getEncryptedCodes(numberOfCodesPerBatch));
			} catch (CryptographyException e) {
				e.printStackTrace();
				// set null to get a new encoder from std crypto
				encoder = null;
				continue;
			}

			CodeListEncoder codeListEncoder = new CodeListEncoder(0,new Random().nextInt(), year,
					this.config.getSubsystemId(), codeList, (int) codeType.getId());

			encoders.add(codeListEncoder);
		}
		return encoders;
	}
}
