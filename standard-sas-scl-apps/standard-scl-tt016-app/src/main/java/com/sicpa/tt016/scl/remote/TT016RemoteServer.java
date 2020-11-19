package com.sicpa.tt016.scl.remote;

import com.sicpa.standard.sasscl.common.storage.IStorage;
import com.sicpa.standard.sasscl.devices.DeviceException;
import com.sicpa.standard.sasscl.devices.IDeviceStatusListener;
import com.sicpa.standard.sasscl.devices.bis.IBisCredentialProvider;
import com.sicpa.standard.sasscl.devices.remote.AbstractRemoteServer;
import com.sicpa.standard.sasscl.devices.remote.GlobalMonitoringToolInfo;
import com.sicpa.standard.sasscl.devices.remote.RemoteServerException;
import com.sicpa.standard.sasscl.devices.remote.connector.AbstractMasterConnector;
import com.sicpa.standard.sasscl.model.EncoderInfo;
import com.sicpa.standard.sasscl.model.PackagedProducts;
import com.sicpa.standard.sasscl.model.ProductStatus;
import com.sicpa.standard.sasscl.monitoring.MonitoringService;
import com.sicpa.standard.sasscl.monitoring.system.event.BasicSystemEvent;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionParameterRootNode;
import com.sicpa.standard.sasscl.sicpadata.generator.FileSequenceStorageProvider;
import com.sicpa.standard.sasscl.sicpadata.generator.IEncoder;
import com.sicpa.standard.sasscl.sicpadata.reader.IAuthenticator;
import com.sicpa.standard.sicpadata.spi.manager.ServiceProviderException;
import com.sicpa.tt016.common.dto.CodingActivationSessionDTO;
import com.sicpa.tt016.common.dto.EncoderInfoDTO;
import com.sicpa.tt016.common.dto.EncoderInfoResultDTO;
import com.sicpa.tt016.common.dto.EncoderInfoResultDTO.InfoResult;
import com.sicpa.tt016.common.dto.EncoderSclDTO;
import com.sicpa.tt016.common.dto.ExportSessionDTO;
import com.sicpa.tt016.common.dto.IEjectionDTO;
import com.sicpa.tt016.common.dto.MaintenanceSessionDTO;
import com.sicpa.tt016.common.dto.OfflineSessionDTO;
import com.sicpa.tt016.common.dto.RefeedSessionDTO;
import com.sicpa.tt016.common.dto.SkuGrossNetProductCounterDTO;
import com.sicpa.tt016.devices.remote.ITT016RemoteServer;
import com.sicpa.tt016.master.scl.exceptions.InternalException;
import com.sicpa.tt016.model.TT016ProductStatus;
import com.sicpa.tt016.scl.remote.assembler.EncryptionConverter;
import com.sicpa.tt016.scl.remote.assembler.ProductionDataConverter;
import com.sicpa.tt016.scl.remote.assembler.SkuConverter;
import com.sicpa.tt016.scl.remote.remoteservices.ITT016RemoteServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.sicpa.standard.sasscl.monitoring.system.SystemEventType.LAST_SENT_TO_REMOTE_SERVER;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class TT016RemoteServer extends AbstractRemoteServer implements IBisCredentialProvider, ITT016RemoteServer {

	private static final Logger logger = LoggerFactory.getLogger(TT016RemoteServer.class);
	// http://psdwiki.sicpa-net.ads/pages/viewpage.action?spaceKey=morocco&title=Development+and+Integration+Servers

	private static final String BIS_USER_NAME = "TRAINER";

	private ITT016RemoteServices remoteServices;
	private AbstractMasterConnector connector;
	private IStorage storage;

	private SkuConverter skuConverter;
	private final EncryptionConverter encryptionConverter = new EncryptionConverter();
	private final ProductionDataConverter productionDataConverter = new ProductionDataConverter();
	
	protected FileSequenceStorageProvider fileSequenceStorageProvider;

	@Override
	public boolean isConnected() {
		return connector.isConnected();
	}

	@Override
	public ProductionParameterRootNode getTreeProductionParameters() throws RemoteServerException {
		if (!isConnected()) {
			return null;
		}
		try {
			return skuConverter.convert(remoteServices.getSkuList());
		} catch (InternalException e) {
			throw new RemoteServerException(e);
		}
	}

	@Override
	public IAuthenticator getAuthenticator() throws RemoteServerException {
		if (!isConnected()) {
			return null;
		}
		return encryptionConverter.convert(remoteServices.getDecoder());
	}

	@Override
	public void sendEncoderInfos(List<EncoderInfo> infos) throws RemoteServerException {

		List<EncoderInfoDTO> dtos = new ArrayList<>();
		for (EncoderInfo i : infos) {
			dtos.add(encryptionConverter.convert(i));
		}
		try {
			EncoderInfoResultDTO res = remoteServices.sendEncoderInfo(dtos);
			handleEncoderInfoResult(res);
		} catch (InternalException e) {
			logger.error("", e);
		}
	}

	private void handleEncoderInfoResult(EncoderInfoResultDTO res) {
		for (InfoResult ir : res.getInfoResult()) {
			if (!ir.isInfoSavedOk()) {
				storage.quarantineEncoder(ir.getBatchId());
				logger.error("master failed to save encoder info for id={} , msg={}", ir.getBatchId() + "",
						ir.getErrorMessage());
			}
		}
	}

	@Override
	public void downloadEncoder(int batchesQuantity, com.sicpa.standard.sasscl.model.CodeType codeType, int year)
			throws RemoteServerException {
		try {
			List<EncoderSclDTO> dtos = remoteServices.getRemoteEncoders(batchesQuantity, (int) codeType.getId());
			for (EncoderSclDTO dto : dtos) {
				IEncoder encoder = encryptionConverter.convert(dto, remoteServices.getSubsystemId(),
						(int) codeType.getId());
				storeEncoder(encoder, year);
				storeSequence(encoder, dto);
			}
		} catch (InternalException e) {
			throw new RemoteServerException(e);
		}
	}

	private void storeEncoder(IEncoder encoder, int year) {
		storage.saveEncoders(year, encoder);
		storage.confirmEncoder(encoder.getId());
	}
	
	private void storeSequence(IEncoder encoder, EncoderSclDTO dto) {
		try {
			fileSequenceStorageProvider.storeSequence(encoder.getBatchId(), dto.getLastUsedSequence());
		} catch (ServiceProviderException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void sendProductionData(PackagedProducts products) throws RemoteServerException {
		try {
			if (products.getProductStatus().equals(ProductStatus.AUTHENTICATED)) {
				sendAuthenticatedData(products);
			} else if (products.getProductStatus().equals(ProductStatus.SENT_TO_PRINTER_UNREAD)
					|| products.getProductStatus().equals(TT016ProductStatus.EJECTED_PRODUCER)
					|| products.getProductStatus().equals(ProductStatus.INK_DETECTED)
					|| products.getProductStatus().equals(ProductStatus.NOT_AUTHENTICATED)
					|| products.getProductStatus().equals(ProductStatus.UNREAD)) {
				sendEjectedData(products);
			} else if (products.getProductStatus().equals(ProductStatus.EXPORT)) {
				sendExportData(products);
			} else if (products.getProductStatus().equals(ProductStatus.MAINTENANCE)) {
				sendMaintenanceData(products);
			} else if (products.getProductStatus().equals(ProductStatus.REFEED)) {
				_sendRefeedtData(products);
			} else if(products.getProductStatus().equals(TT016ProductStatus.REFEED_NO_INK)){
				sendRefeedEjectedData(products);
			} else if (products.getProductStatus().equals(ProductStatus.OFFLINE)) {
				sendOfflineCountingData(products);
			} else {
				logger.warn("package not handled:" + products.getProductStatus());
			}
			registerSendDataSystemEvent(products);
		} catch (Exception e) {
			throw new RemoteServerException("", e);
		}
	}
	
	@Override
	public void sendSkuGrossNetProductCounter(SkuGrossNetProductCounterDTO[] skuGrossNetProductArray) throws InternalException {
		List<SkuGrossNetProductCounterDTO> skuGrossNetProductList = Arrays.asList(skuGrossNetProductArray);
		remoteServices.sendSkuGrossNetProductCounter(skuGrossNetProductList);
	}
	
	private void registerSendDataSystemEvent(PackagedProducts products) {
		int productCount = products.getProducts().size();
		MonitoringService
				.addSystemEvent(new BasicSystemEvent(LAST_SENT_TO_REMOTE_SERVER, String.valueOf(productCount)));
	}

	private void sendAuthenticatedData(PackagedProducts products) throws InternalException {
		CodingActivationSessionDTO data = productionDataConverter.convertAuthenticated(products,
				remoteServices.getSubsystemId());
		remoteServices.sendDomesticProduction(data);
	}

	private void sendRefeedData(PackagedProducts products) throws InternalException {
		CodingActivationSessionDTO data = productionDataConverter.convertAuthenticated(products,
				remoteServices.getSubsystemId());
		remoteServices.sendRefeedProduction(data);
	}

	private void sendEjectedData(PackagedProducts products) throws InternalException {
		IEjectionDTO data = productionDataConverter.convertEjection(products, remoteServices.getSubsystemId());
		remoteServices.sendEjectedProduction(data);
	}
	
	
	private void sendRefeedEjectedData(PackagedProducts products) throws InternalException {
		IEjectionDTO data = productionDataConverter.convertRefeedEjection(products, remoteServices.getSubsystemId());
		remoteServices.sendRefeedEjectedProduction(data);
	}

	private void sendMaintenanceData(PackagedProducts products) throws InternalException {
		MaintenanceSessionDTO data = productionDataConverter.convertMaintenance(products,
				remoteServices.getSubsystemId());
		remoteServices.sendMaintenanceProduction(data);
	}

	private void sendExportData(PackagedProducts products) throws InternalException {
		ExportSessionDTO data = productionDataConverter.convertExport(products, remoteServices.getSubsystemId());
		remoteServices.sendExportProduction(data);
	}
	
	/** 
	 * @param products
	 * @throws InternalException
	 */
	private void _sendRefeedtData(PackagedProducts products) throws InternalException {
		RefeedSessionDTO data = productionDataConverter.convertRefeed(products, remoteServices.getSubsystemId());
		remoteServices.sendRefeedProduction(data);
	}

	private void sendOfflineCountingData(PackagedProducts products) throws InternalException {
		OfflineSessionDTO data = productionDataConverter.convertOffline(products, remoteServices.getSubsystemId());

		remoteServices.sendOfflineProduction(data);
	}
	
	@Override
	public Map<String, ? extends ResourceBundle> getLanguageBundles() throws RemoteServerException {
		return null;
	}

	@Override
	public long getSubsystemID() {
		return remoteServices.getSubsystemId();
	}

	@Override
	public void sendInfoToGlobalMonitoringTool(GlobalMonitoringToolInfo info) {
	}

	@Override
	protected void doConnect() {
		connector.doConnect();
	}

	@Override
	protected void doDisconnect() {
		try {
			connector.doDisconnect();
		} catch (DeviceException e) {
			logger.error("", e);
		}
	}

	public void setRemoteServices(ITT016RemoteServices remoteServices) {
		this.remoteServices = remoteServices;
	}

	public void setConnector(AbstractMasterConnector connector) {
		this.connector = connector;
	}

	public void setSkuConverter(SkuConverter skuConverter) {
		this.skuConverter = skuConverter;
	}

	@Override
	public void addDeviceStatusListener(IDeviceStatusListener listener) {
		super.addDeviceStatusListener(listener);
		connector.addDeviceStatusListener(listener);
	}

	public void setStorage(IStorage storage) {
		this.storage = storage;
	}

	@Override
	public String getUserName() {
		return BIS_USER_NAME;
	}

	@Override
	public String getPassword() {
		return remoteServices.getBisTrainerPassword(getUserName());
	}
	
	public void setFileSequenceStorageProvider(FileSequenceStorageProvider fileSequenceStorageProvider) {
		this.fileSequenceStorageProvider = fileSequenceStorageProvider;
	}

}
