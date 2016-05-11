package com.sicpa.tt018.scl.remoteServer.simu;

import static com.sicpa.tt018.scl.remoteServer.utilities.AlbaniaRemoteServerUtilities.wrapEncoderList;
import static com.sicpa.tt018.scl.remoteServer.utilities.AlbaniaRemoteServerValidator.validateEncoders;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.model.PackagedProducts;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionParameterRootNode;
import com.sicpa.standard.sasscl.sicpadata.generator.IEncoder;
import com.sicpa.standard.sasscl.sicpadata.reader.IAuthenticator;
import com.sicpa.tt018.interfaces.scl.master.dto.AlbaniaEncoderDTO;
import com.sicpa.tt018.interfaces.scl.master.dto.MarketTypeDTO;
import com.sicpa.tt018.interfaces.security.IAlbaniaAuthenticator;
import com.sicpa.tt018.scl.model.authenticator.AlbaniaAuthenticatorWrapper;
import com.sicpa.tt018.scl.remoteServer.AlbaniaRemoteServer;

public class AlbaniaRemoteServerSimulator extends AlbaniaRemoteServer {

	private static final Logger logger = LoggerFactory.getLogger(AlbaniaRemoteServerSimulator.class);

	private AlbaniaRemoteServerModelSimulator simulatorModel;

	public AlbaniaRemoteServerSimulator() {
		super();
	}

	@Override
	public ProductionParameterRootNode doGetTreeProductionParameters() {
		MarketTypeDTO marketTypeDTO = simulatorModel.getMarketTypeDTO();
		fireDeviceStatusChanged(DeviceStatus.CONNECTED);
		return remoteServerAdapter.createSkuSelectionTree(marketTypeDTO);
	}

	@Override
	protected void processActivatedProducts(PackagedProducts products) {
		remoteServerAdapter.convertDomesticProduction(products, subSystemId);
	}

	@Override
	protected void processCountedProducts(PackagedProducts products) {
		remoteServerAdapter.convertCountingProduction(products, subSystemId);
	}

	@Override
	public IAuthenticator doGetAuthenticator() {
		logger.debug("Retrieveing authenticator from master....");
		IAlbaniaAuthenticator albaniaAuthenticator = simulatorModel.loadSimuAuthenticator();
		fireDeviceStatusChanged(DeviceStatus.CONNECTED);
		return new AlbaniaAuthenticatorWrapper(albaniaAuthenticator, cryptoPassword, cryptoFieldsConfig);

	}

	public void doDownloadEncoder(int quantity, CodeType codeType, int year) {
		try {
			List<AlbaniaEncoderDTO> encoders = simulatorModel.provideEncoders(quantity, (int) codeType.getId(),
					subSystemId);
			fireDeviceStatusChanged(DeviceStatus.CONNECTED);
			validateEncoders(encoders, quantity, subSystemId);
			List<IEncoder> listEncoders = wrapEncoderList(encoders, year, subSystemId, getCryptoFieldsConfig(),
					getCryptoPassword(), codeType);

			for (IEncoder encoder : listEncoders) {
				storeEncoder(encoder, year);
			}

		} catch (Exception e) {
			logger.error("", e);
		}
	}

	public void setSimulatorModel(AlbaniaRemoteServerModelSimulator simulatorModel) {
		this.simulatorModel = simulatorModel;
	}
}
