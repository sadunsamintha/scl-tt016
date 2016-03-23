package com.sicpa.tt018.scl.remoteServer.simu;

import java.text.MessageFormat;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.client.common.utils.ConfigUtils;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.devices.remote.impl.RemoteServerModel;
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
import com.sicpa.tt018.scl.remoteServer.utilities.AlbaniaRemoteServerUtilities;
import com.sicpa.tt018.scl.remoteServer.utilities.AlbaniaRemoteServerValidator;
import com.sicpa.tt018.scl.utils.AlbaniaUtilities;
import com.sicpa.tt018.scl.utils.ValidatorException;

public class AlbaniaRemoteServerSimulator extends AlbaniaRemoteServer {

	private static final Logger logger = LoggerFactory.getLogger(AlbaniaRemoteServerSimulator.class);

	private AlbaniaRemoteServerModelSimulator remoteServerSimulatorModel;
	private String pathToRemoteServerFile;

	public AlbaniaRemoteServerSimulator() {
		super(new RemoteServerModel());
	}

	public void loadModel() {
		remoteServerSimulatorModel = loadRemoteServerSimulator();
	}

	@Override
	public ProductionParameterRootNode doGetTreeProductionParameters() {
		logger.debug("Retrieveing SKU list from master....");

		MarketTypeDTO marketTypeDTO = remoteServerSimulatorModel.getMarketTypeDTO();

		// MarketTypeDTO received ???
		if (AlbaniaRemoteServerUtilities.isEmpty(marketTypeDTO)) {
			logger.debug("MarketType recieved from master is NULL.");

		} else {
			logger.debug("MarketType recieved [id= {} ,desc= {}]", marketTypeDTO.getId(),
					marketTypeDTO.getDescription());
		}

		// No error... Master connected
		fireDeviceStatusChanged(DeviceStatus.CONNECTED);

		// Convert the result
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

		final IAlbaniaAuthenticator albaniaAuthenticator = remoteServerSimulatorModel.loadSimuAuthenticator();

		fireDeviceStatusChanged(DeviceStatus.CONNECTED);

		// Wrap the result
		return new AlbaniaAuthenticatorWrapper(albaniaAuthenticator, cryptoPassword, cryptoFieldsConfig);

	}

	public void doDownloadEncoder(final int quantity, final CodeType codeType, final int year) {
		if (quantity <= 0) {
			return;
		}
		logger.info("requesting encoder qty={} codeType={} ,  year={}",
				new Object[] { quantity, codeType.getId(), year });

		try {
			// Get encoders from master
			final List<AlbaniaEncoderDTO> encoders = remoteServerSimulatorModel.provideEncoders(quantity,
					(int) codeType.getId(), subSystemId);

			// Encoders received ???
			if (AlbaniaUtilities.isEmpty(encoders)) {
				logger.error("Encoder recieved from master is NULL.");
				EventBusService.post(new MessageEvent("encoder.received.null"));

			}
			logger.debug("Encoders recieved. Number of encoders = {}.", encoders.size());

			// No error... Master connected
			fireDeviceStatusChanged(DeviceStatus.CONNECTED);

			// Encoder list validation
			AlbaniaRemoteServerValidator.validateEncoders(encoders, quantity, subSystemId);

			// Return wrapped list
			List<IEncoder> listEncoders = AlbaniaRemoteServerUtilities.wrapEncoderList(encoders, year, subSystemId,
					getCryptoFieldsConfig(), getCryptoPassword(), codeType);

			for (IEncoder encoder : listEncoders) {
				storeEncoder(encoder, year);
			}

		} catch (final ValidatorException e)
		// Validation Exception
		{
			logger.error(MessageFormat.format("request SicpadataGenerator, quantity:{} codeType:{}  year:{}, failed",
					quantity, codeType.getId(), year), e);
			EventBusService.post(new MessageEvent("remoteserver.getEncoder.fail"));
			logger.error("Error during encoder validation = {0}.");
			EventBusService.post(new MessageEvent("encoder.validation.exception"));

		} catch (final Exception e)
		// Master communication Exception
		{
			logger.error(MessageFormat.format("request SicpadataGenerator, quantity:{} codeType:{}  year:{}, failed",
					quantity, codeType.getId(), year), e);
			EventBusService.post(new MessageEvent("remoteserver.getEncoder.fail"));
			logger.error("Error while retrieving encoder from master = {0}.");
			// Error... Master connection problem...
			fireDeviceStatusChanged(DeviceStatus.DISCONNECTED);
			EventBusService.post(new MessageEvent("encoder.retrieving.exception"));
		}
	}

	private AlbaniaRemoteServerModelSimulator loadRemoteServerSimulator() {

		try {

			AlbaniaRemoteServerXStream.configureXstreamAlbaniaRemoteServerSimuModel();
			AlbaniaRemoteServerModelSimulator remoteServerSimulatorModel = (AlbaniaRemoteServerModelSimulator) ConfigUtils
					.load(pathToRemoteServerFile);

			return remoteServerSimulatorModel;
		} catch (Exception e) {
			logger.error("Fail to load : " + pathToRemoteServerFile);
			return null;
		}

	}

	public String getPathToRemoteServerFile() {
		return pathToRemoteServerFile;
	}

	public void setPathToRemoteServerFile(String pathToRemoteServerFile) {
		this.pathToRemoteServerFile = pathToRemoteServerFile;
	}

}
