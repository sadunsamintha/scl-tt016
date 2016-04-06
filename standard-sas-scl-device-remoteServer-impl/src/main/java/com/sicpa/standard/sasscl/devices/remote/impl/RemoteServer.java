package com.sicpa.standard.sasscl.devices.remote.impl;

import static com.sicpa.standard.client.common.utils.PropertiesUtils.savePropertiesKeepOrderAndComment;
import static com.sicpa.standard.sasscl.monitoring.system.SystemEventType.LAST_SENT_TO_REMOTE_SERVER;
import static com.sicpa.standard.sasscl.utils.ConfigUtilEx.GLOBAL_PROPERTIES_PATH;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.configuration.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.common.storage.IStorage;
import com.sicpa.standard.sasscl.devices.DeviceException;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.devices.IDeviceStatusListener;
import com.sicpa.standard.sasscl.devices.remote.AbstractRemoteServer;
import com.sicpa.standard.sasscl.devices.remote.GlobalMonitoringToolInfo;
import com.sicpa.standard.sasscl.devices.remote.RemoteServerException;
import com.sicpa.standard.sasscl.devices.remote.connector.AbstractMasterConnector;
import com.sicpa.standard.sasscl.devices.remote.connector.IConnectable;
import com.sicpa.standard.sasscl.devices.remote.datasender.DataRegisteringException;
import com.sicpa.standard.sasscl.devices.remote.datasender.IPackageSender;
import com.sicpa.standard.sasscl.devices.remote.impl.dtoConverter.IDtoConverter;
import com.sicpa.standard.sasscl.devices.remote.impl.remoteservices.IRemoteServices;
import com.sicpa.standard.sasscl.devices.remote.impl.sicpadata.ISicpaDataGeneratorRequestor;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.model.EncoderInfo;
import com.sicpa.standard.sasscl.model.PackagedProducts;
import com.sicpa.standard.sasscl.monitoring.MonitoringService;
import com.sicpa.standard.sasscl.monitoring.system.event.BasicSystemEvent;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionParameterRootNode;
import com.sicpa.standard.sasscl.sicpadata.CryptoServiceProviderManager;
import com.sicpa.standard.sasscl.sicpadata.reader.IAuthenticator;
import com.sicpa.std.common.api.activation.dto.AuthorizedProductsDto;
import com.sicpa.std.common.api.activation.dto.SicpadataReaderDto;
import com.sicpa.std.common.api.activation.exception.ActivationException;
import com.sicpa.std.common.api.coding.dto.SicpadataGeneratorInfoDto;
import com.sicpa.std.common.api.coding.dto.SicpadataGeneratorInfoResultDto;
import com.sicpa.std.common.api.coding.dto.SicpadataGeneratorInfoResultDto.InfoResult;
import com.sicpa.std.common.api.monitoring.dto.EventDto;
import com.sicpa.std.common.api.multilingual.dto.AvailableLanguageDto;

public class RemoteServer extends AbstractRemoteServer implements IConnectable {

	private static final Logger logger = LoggerFactory.getLogger(RemoteServer.class);

	private CryptoServiceProviderManager cryptoServiceProviderManager;

	private ISicpaDataGeneratorRequestor sdGenReceiver;
	private IStorage storage;

	private IRemoteServices remoteServices;
	private IPackageSender packageSender;
	private IDtoConverter converter;
	private AbstractMasterConnector connector;

	@Override
	protected void doConnect() throws RemoteServerException {
		connector.doConnect();
	}

	public void doLogin() {
		connector.doLogin();
	}

	@Override
	public void isAlive() {
		connector.isAlive();
	}

	@Override
	protected void doDisconnect() throws RemoteServerException {
		try {
			connector.doDisconnect();
		} catch (DeviceException e) {
			throw new RemoteServerException(e);
		}
	}

	public boolean isConnected() {
		return connector.isConnected();
	}

	@Override
	public final IAuthenticator getAuthenticator() throws RemoteServerException {
		if (!isConnected()) {
			return null;
		}
		try {
			return doGetAuthenticator();
		} catch (Exception e) {
			throw new RemoteServerException(e);
		}
	}

	private IAuthenticator doGetAuthenticator() throws ActivationException {
		getAndSaveCryptoPassword();
		SicpadataReaderDto auth = remoteServices.provideSicpadataReader();
		return converter.convert(auth);
	}

	@Override
	public final void downloadEncoder(int batchesQuantity, CodeType codeType, int year) throws RemoteServerException {
		if (!isConnected()) {
			return;
		}
		try {
			doDownloadEncoder(batchesQuantity, codeType, year);
		} catch (Exception e) {
			throw new RemoteServerException(e);
		}
	}

	private void getAndSaveCryptoPassword() {
		try {
			String pwd = remoteServices.getCryptoPassword();
			cryptoServiceProviderManager.setPassword(pwd);
			saveCryptoPassword(pwd);
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	private void saveCryptoPassword(String pwd) throws ConfigurationException, IOException {
		savePropertiesKeepOrderAndComment(new ClassPathResource(GLOBAL_PROPERTIES_PATH).getFile(), "sicpadataPassword",
				"" + pwd);
	}

	private void doDownloadEncoder(int quantity, CodeType codeType, int year) {
		if (quantity <= 0) {
			return;
		}
		logger.info("requesting encoder qty={} codeType={} ,  year={}",
				new Object[] { quantity, codeType.getId(), year });
		try {

			sdGenReceiver.requestSicpadataGenerators(converter.newEncoderRequestOrder(quantity, codeType, year),
					remoteServices.getCodingService());
		} catch (Exception e) {
			logger.error(MessageFormat.format(
					"request SicpadataGenerator, quantity:{0} codeType:{1}  year:{2}, failed", quantity,
					codeType.getId(), year), e);
			EventBusService.post(new MessageEvent("remoteserver.getEncoder.fail"));
		}
	}

	@Override
	public final ProductionParameterRootNode getTreeProductionParameters() throws RemoteServerException {
		if (!isConnected()) {
			return null;
		}
		try {
			AuthorizedProductsDto products = remoteServices.provideAuthorizedProducts();
			return converter.convert(products);
		} catch (Exception e) {
			throw new RemoteServerException(e);
		}
	}

	@Override
	public void sendEncoderInfos(List<EncoderInfo> infos) throws RemoteServerException {
		try {
			List<SicpadataGeneratorInfoDto> dtos = converter.createEncoderInfo(infos);
			logger.info("sending encoder info {}", infos);
			SicpadataGeneratorInfoResultDto res = remoteServices.registerGeneratorsCycle(dtos);
			handleEncoderInfoResult(res);
		} catch (Exception e) {
			throw new RemoteServerException(e);
		}
	}

	private void handleEncoderInfoResult(SicpadataGeneratorInfoResultDto res) throws RemoteServerException {
		for (InfoResult ir : res.getInfoResult()) {
			if (!ir.isInfoSavedOk()) {
				storage.quarantineEncoder(ir.getId());
				throw new RemoteServerException(MessageFormat.format(
						"master failed to save encoder info for id={0} , msg={1}", ir.getId() + "",
						ir.getErrorMessage()));
			}
		}
	}

	@Override
	public final void sendProductionData(PackagedProducts products) throws RemoteServerException {
		if (!isConnected()) {
			throw new RemoteServerException();
		}
		if (products == null || isEmpty(products.getProducts())) {
			return;
		}
		try {
			registerSendDataSystemEvent(products);
			packageSender.sendPackage(products);
		} catch (Exception e) {
			RemoteServerException ex = new RemoteServerException(e);
			ex.setBusiness(e instanceof DataRegisteringException);
			throw ex;
		}
	}

	private void registerSendDataSystemEvent(PackagedProducts products) {
		int productCount = products.getProducts().size();
		MonitoringService
				.addSystemEvent(new BasicSystemEvent(LAST_SENT_TO_REMOTE_SERVER, String.valueOf(productCount)));
	}

	private void checkConnection() throws RemoteServerException {
		try {
			remoteServices.isAlive();
		} catch (Exception e) {
			throw new RemoteServerException(e);
		}
	}

	@Override

	public long getSubsystemID() {
		try {
			return remoteServices.getSubsystem().getId();
		} catch (Exception e) {
			logger.error("failed to get subsystem id", e);
		}
		return 0;
	}

	@Override
	public void lifeCheckTick() {
		try {
			logger.debug("remote server life check");
			checkConnection();
			fireDeviceStatusChanged(DeviceStatus.CONNECTED);
		} catch (Exception e) {
			logger.error("", e);
			fireDeviceStatusChanged(DeviceStatus.DISCONNECTED);
		}
	}

	@Override
	public Map<String, ? extends ResourceBundle> getLanguageBundles() throws RemoteServerException {
		if (!isConnected()) {
			return null;
		}
		try {
			return doGetTranslationBundles();
		} catch (Exception e) {
			throw new RemoteServerException(e);
		}
	}

	private Map<String, ResourceBundle> doGetTranslationBundles() {
		Map<String, ResourceBundle> res = new HashMap<>();
		try {
			for (AvailableLanguageDto lang : remoteServices.getAvailableLanguages()) {
				res.put(lang.getCode(), remoteServices.getResourceBundle(lang.getCode()));
			}
			return res;
		} catch (Exception e) {
			logger.error("", e);
		}
		return res;
	}

	public void setCryptoServiceProviderManager(CryptoServiceProviderManager cryptoServiceProviderManager) {
		this.cryptoServiceProviderManager = cryptoServiceProviderManager;
	}

	public void setSdGenReceiver(ISicpaDataGeneratorRequestor sdGenReceiver) {
		this.sdGenReceiver = sdGenReceiver;
	}

	public void setStorage(IStorage storage) {
		this.storage = storage;
	}

	@Override
	public void sendInfoToGlobalMonitoringTool(GlobalMonitoringToolInfo info) {
		try {
			EventDto evt = converter.createEventDto(info);
			remoteServices.registerEvent(evt);
		} catch (Exception e) {
			logger.error("error sending global monitoring tool info:" + info, e);
		}
	}

	public void setRemoteServices(IRemoteServices remoteServices) {
		this.remoteServices = remoteServices;
	}

	public void setConverter(IDtoConverter converter) {
		this.converter = converter;
	}

	@Override
	public void addDeviceStatusListener(IDeviceStatusListener listener) {
		super.addDeviceStatusListener(listener);
		connector.addDeviceStatusListener(listener);
	}

	public void setConnector(AbstractMasterConnector connector) {
		this.connector = connector;
	}

	@Override
	public void removeDeviceStatusListener(IDeviceStatusListener listener) {
		super.removeDeviceStatusListener(listener);
		connector.removeDeviceStatusListener(listener);
	}

	public void setPackageSender(IPackageSender packageSender) {
		this.packageSender = packageSender;
	}
}
