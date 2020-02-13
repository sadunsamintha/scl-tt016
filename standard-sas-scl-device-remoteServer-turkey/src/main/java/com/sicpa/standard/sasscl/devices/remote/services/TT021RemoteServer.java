package com.sicpa.standard.sasscl.devices.remote.services;

import static com.sicpa.standard.client.common.utils.PropertiesUtils.savePropertiesKeepOrderAndComment;
import static com.sicpa.standard.sasscl.monitoring.system.SystemEventType.LAST_SENT_TO_REMOTE_SERVER;
import static com.sicpa.standard.sasscl.utils.ConfigUtilEx.GLOBAL_PROPERTIES_PATH;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.configuration.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import com.sicpa.standard.sasscl.common.storage.IStorage;
import com.sicpa.standard.sasscl.devices.DeviceException;
import com.sicpa.standard.sasscl.devices.IDeviceStatusListener;
import com.sicpa.standard.sasscl.devices.remote.AbstractRemoteServer;
import com.sicpa.standard.sasscl.devices.remote.GlobalMonitoringToolInfo;
import com.sicpa.standard.sasscl.devices.remote.RemoteServerException;
import com.sicpa.standard.sasscl.devices.remote.connector.AbstractMasterConnector;
import com.sicpa.standard.sasscl.devices.remote.connector.IConnectable;
import com.sicpa.standard.sasscl.devices.remote.datasender.DataRegisteringException;
import com.sicpa.standard.sasscl.devices.remote.datasender.IPackageSender;
import com.sicpa.standard.sasscl.devices.remote.impl.dtoConverter.IDtoConverter;
import com.sicpa.standard.sasscl.devices.remote.impl.remoteservices.ITT021RemoteServices;
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

public class TT021RemoteServer extends AbstractRemoteServer implements IConnectable {

	private static final Logger logger = LoggerFactory.getLogger(TT021RemoteServer.class);
	
	private ITT021RemoteServices remoteServices;
	
	private AbstractMasterConnector connector;
	private IDtoConverter converter;
	private IStorage storage;
	private IPackageSender packageSender;
	private CryptoServiceProviderManager cryptoServiceProviderManager;
	
	private ISicpaDataGeneratorRequestor sdGenReceiver;
	
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
	public ProductionParameterRootNode getTreeProductionParameters() throws RemoteServerException {
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
	
	private void doDownloadEncoder(int quantity, CodeType codeType, int year) {
//		if (quantity <= 0) {
//			return;
//		}
//		logger.info("requesting encoder qty={} codeType={} ,  year={}",
//				new Object[] { quantity, codeType.getId(), year });
//		try {
//
//			sdGenReceiver.requestSicpadataGenerators(converter.newEncoderRequestOrder(quantity, codeType, year),
//					remoteServices.getCodingService());
//		} catch (Exception e) {
//			logger.error(MessageFormat.format(
//					"request SicpadataGenerator, quantity:{0} codeType:{1}  year:{2}, failed", quantity,
//					codeType.getId(), year), e);
//			EventBusService.post(new MessageEvent("remoteserver.getEncoder.fail"));
//		}
	}

	@Override
	public IAuthenticator getAuthenticator() throws RemoteServerException {
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
	
	private void getAndSaveCryptoPassword() {
		try {
			String pwd = "admin";
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
	
	@Override
	public void sendProductionData(PackagedProducts products) throws RemoteServerException {
		if (!isConnected()) {
			throw new RemoteServerException();
		}
		if (products == null || isEmpty(products.getProducts())) {
			return;
		}
		try {
			//registerSendDataSystemEvent(products);
			logger.info("TT021RemoteServer - sendProductionData...");
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
	
	@Override
	public Map<String, ? extends ResourceBundle> getLanguageBundles() throws RemoteServerException {
		// TODO Auto-generated method stub
		return null;
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
	
	private void handleEncoderInfoResult(SicpadataGeneratorInfoResultDto res) {
		for (InfoResult ir : res.getInfoResult()) {
			if (!ir.isInfoSavedOk()) {
				storage.quarantineEncoder(ir.getId());
				logger.error("master failed to save encoder info for id={} , msg={}", ir.getId() + "",
						ir.getErrorMessage());
			}
		}
	}

	@Override
	public long getSubsystemID() {
		try {
			return remoteServices.getSubsystem().getId();
		} catch (Exception e) {
			logger.error("failed to get subsystem id", e);
		}
		return ERROR_DEFAULT_SUBSYSTEM_ID;
	}
	
	@Override
	public void sendInfoToGlobalMonitoringTool(GlobalMonitoringToolInfo info) {
		// TODO Auto-generated method stub
		
	}

	public void setRemoteServices(ITT021RemoteServices remoteServices) {
		this.remoteServices = remoteServices;
	}
	
	public void setConnector(AbstractMasterConnector connector) {
		this.connector = connector;
	}
	
	@Override
	public void addDeviceStatusListener(IDeviceStatusListener listener) {
		super.addDeviceStatusListener(listener);
		connector.addDeviceStatusListener(listener);
	}
	
	public void setConverter(IDtoConverter converter) {
		this.converter = converter;
	}
	
	public void setCryptoServiceProviderManager(CryptoServiceProviderManager cryptoServiceProviderManager) {
		this.cryptoServiceProviderManager = cryptoServiceProviderManager;
	}	
	public void setStorage(IStorage storage) {
		this.storage = storage;
	}

	public void setSdGenReceiver(ISicpaDataGeneratorRequestor sdGenReceiver) {
		this.sdGenReceiver = sdGenReceiver;
	}
	
	public void setPackageSender(IPackageSender packageSender) {
		this.packageSender = packageSender;
	}
}
