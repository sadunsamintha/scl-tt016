package com.sicpa.tt018.scl.remoteServer;

import java.text.MessageFormat;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.client.common.timeout.Timeout;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.devices.remote.RemoteServerException;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.model.PackagedProducts;
import com.sicpa.standard.sasscl.model.ProductStatus;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionParameterRootNode;
import com.sicpa.standard.sasscl.sicpadata.generator.IEncoder;
import com.sicpa.standard.sasscl.sicpadata.reader.IAuthenticator;
import com.sicpa.tt018.interfaces.scl.master.ICodingActivationRemote;
import com.sicpa.tt018.interfaces.scl.master.dto.AlbaniaEncoderDTO;
import com.sicpa.tt018.interfaces.scl.master.dto.CountedProductsDTO;
import com.sicpa.tt018.interfaces.scl.master.dto.MarketTypeDTO;
import com.sicpa.tt018.interfaces.scl.master.dto.PackagedProductsDTO;
import com.sicpa.tt018.interfaces.security.IAlbaniaAuthenticator;
import com.sicpa.tt018.scl.model.AlbaniaProductStatus;
import com.sicpa.tt018.scl.model.authenticator.AlbaniaAuthenticatorWrapper;
import com.sicpa.tt018.scl.remoteServer.adapter.IAlblaniaRemoteServerAdapter;
import com.sicpa.tt018.scl.remoteServer.utilities.AlbaniaRemoteServerUtilities;
import com.sicpa.tt018.scl.remoteServer.utilities.AlbaniaRemoteServerValidator;
import com.sicpa.tt018.scl.utils.AlbaniaUtilities;
import com.sicpa.tt018.scl.utils.ValidatorException;

public class AlbaniaRemoteServer extends RemoteServer {

	public AlbaniaRemoteServer() {
		super();
		initPackageSenders();
	}

	private static Logger logger = LoggerFactory.getLogger(AlbaniaRemoteServer.class);

	// for albania only one ejb for everything
	// no login bean available
	private ICodingActivationRemote ejbCodingActivationBean;
	protected IAlblaniaRemoteServerAdapter remoteServerAdapter;
	protected int subSystemId;
	protected String cryptoPassword;

	public void checkConnection() throws RemoteServerException {
		try {
			// Call one method from Master Bean
			tryGetTheTree();
		} catch (Exception e) {
			throw new RemoteServerException(e);
		}
	}

	protected void initPackageSenders() {

		IPackageSender senderActivated = (products) -> processActivatedProducts(products);
		IPackageSender senderCounted = (products) -> processCountedProducts(products);

		packageSenders.put(ProductStatus.AUTHENTICATED, senderActivated);
		packageSenders.put(ProductStatus.REFEED, senderActivated);
		packageSenders.put(ProductStatus.SENT_TO_PRINTER_UNREAD, senderActivated);
		packageSenders.put(ProductStatus.SENT_TO_PRINTER_WASTED, senderActivated);
		packageSenders.put(ProductStatus.TYPE_MISMATCH, senderActivated);

		packageSenders.put(ProductStatus.COUNTING, senderCounted);
		packageSenders.put(ProductStatus.EXPORT, senderCounted);
		packageSenders.put(ProductStatus.MAINTENANCE, senderCounted);

		packageSenders.put(ProductStatus.NOT_AUTHENTICATED, senderCounted);
		packageSenders.put(ProductStatus.UNREAD, senderCounted);

		packageSenders.put(AlbaniaProductStatus.SOFT_DRINK, senderCounted);

	}

	private ProductionParameterRootNode tryGetTheTree() throws RemoteServerException {
		try {
			return doGetTreeProductionParameters();
		} catch (Exception e) {
			throw new RemoteServerException(e);
		}

	}

	@Override
	public ProductionParameterRootNode doGetTreeProductionParameters() {
		logger.debug("Retrieveing SKU list from master....");

		MarketTypeDTO marketTypeDTO = ejbCodingActivationBean.provideAuthorizedProducts(subSystemId);

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
		logger.debug("Sending activated production data for packagedProducts = {} in file = {}", products.getUID(),
				products.getFileName());

		PackagedProductsDTO domesticProduction = remoteServerAdapter.convertDomesticProduction(products, subSystemId);
		ejbCodingActivationBean.saveProductionData(domesticProduction, subSystemId);

		logger.debug("Production data send for packagedProducts = {}", products.getUID());
	}

	@Override
	protected void processCountedProducts(PackagedProducts products) {
		logger.debug("Sending production data for packagedProducts = {} in file = {}", products.getUID(),
				products.getFileName());

		CountedProductsDTO countingProduction = remoteServerAdapter.convertCountingProduction(products, subSystemId);
		ejbCodingActivationBean.saveCountedProductionData(countingProduction, subSystemId);

		logger.debug("Production data send for packagedProducts = {}", products.getUID());
	}

	@Override
	public IAuthenticator doGetAuthenticator() {

		logger.debug("Retrieveing authenticator from master....");

		try {
			IAlbaniaAuthenticator albaniaAuthenticator = ejbCodingActivationBean.provideAuthenticator();

			// Authenticator received ???
			if (albaniaAuthenticator == null) {
				logger.debug("Authenticator recieved from master is NULL.");
				return null;
			}

			logger.debug("Authenticator recieved [class = {}]", albaniaAuthenticator.getClass());

			fireDeviceStatusChanged(DeviceStatus.CONNECTED);
			return new AlbaniaAuthenticatorWrapper(albaniaAuthenticator, cryptoPassword, getCryptoFieldsConfig());

		} catch (Exception e) {
			logger.error("Error while retrieving authenticator from master = {}.", e);
			// Error... Master connection problem...
			fireDeviceStatusChanged(DeviceStatus.DISCONNECTED);
		}
		return null;
	}

	public void doDownloadEncoder(final int quantity, final CodeType codeType, final int year) {
		if (quantity <= 0) {
			return;
		}
		logger.info("requesting encoder qty={} codeType={} ,  year={}",
				new Object[] { quantity, codeType.getId(), year });

		try {
			// Get encoders from master
			final List<AlbaniaEncoderDTO> encoders = ejbCodingActivationBean.provideEncoders(quantity,
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

		} catch (ValidatorException e) {
			logger.error(MessageFormat.format(
					"request SicpadataGenerator, quantity:{0} codeType:{1}  year:{2}, failed", quantity,
					codeType.getId(), year), e);
			EventBusService.post(new MessageEvent("remoteserver.getEncoder.fail"));
			logger.error("Error during encoder validation = {0}.");
			EventBusService.post(new MessageEvent("encoder.validation.exception"));

		} catch (final Exception e)
		// Master communication Exception
		{
			logger.error(MessageFormat.format(
					"request SicpadataGenerator, quantity:{0} codeType:{1}  year:{2}, failed", quantity,
					codeType.getId(), year), e);
			EventBusService.post(new MessageEvent("remoteserver.getEncoder.fail"));
			logger.error("Error while retrieving encoder from master = {0}.");
			// Error... Master connection problem...
			fireDeviceStatusChanged(DeviceStatus.DISCONNECTED);
			EventBusService.post(new MessageEvent("encoder.retrieving.exception"));
		}
	}

	protected void storeEncoder(IEncoder encoder, int year) {
		storage.saveEncoders(year, encoder);
		storage.confirmEncoder(encoder.getId());

	}

	public void setEjbCodingActivationBean(final ICodingActivationRemote bean) {
		ejbCodingActivationBean = bean;
	}

	protected ICodingActivationRemote getCodingActivationBean() {
		return ejbCodingActivationBean;
	}

	public void setRemoteServerAdapter(final IAlblaniaRemoteServerAdapter adapter) {
		remoteServerAdapter = adapter;
	}

	protected IAlblaniaRemoteServerAdapter getRemoteServerAdapter() {
		return remoteServerAdapter;
	}

	public void setSubSystemId(int subSystemId) {
		this.subSystemId = subSystemId;
	}

	public String getCryptoPassword() {
		return cryptoPassword;
	}

	public void setCryptoPassword(String cryptoPassword) {
		this.cryptoPassword = cryptoPassword;
	}

	@Override
	@Timeout
	public long getSubsystemID() {
		return subSystemId;
	}
}
