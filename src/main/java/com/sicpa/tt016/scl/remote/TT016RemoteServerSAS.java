package com.sicpa.tt016.scl.remote;

import static com.sicpa.standard.sasscl.monitoring.system.SystemEventType.LAST_SENT_TO_REMOTE_SERVER;

import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.monitoring.MonitoringService;
import com.sicpa.standard.sasscl.monitoring.system.event.BasicSystemEvent;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionParameterRootNode;
import com.sicpa.standard.sasscl.sicpadata.reader.IAuthenticator;
import com.sicpa.tt016.common.dto.ActivationSessionDTO;
import com.sicpa.tt016.common.dto.AgedWineSessionDTO;
import com.sicpa.tt016.common.dto.ExportSessionDTO;
import com.sicpa.tt016.common.dto.IEjectionDTO;
import com.sicpa.tt016.common.dto.MaintenanceSessionDTO;
import com.sicpa.tt016.common.dto.OfflineSessionDTO;
import com.sicpa.tt016.master.scl.exceptions.InternalException;
import com.sicpa.tt016.model.TT016ProductStatus;
import com.sicpa.tt016.scl.remote.assembler.EncryptionConverter;
import com.sicpa.tt016.scl.remote.assembler.ProductionDataConverter;
import com.sicpa.tt016.scl.remote.assembler.SkuConverter;
import com.sicpa.tt016.scl.remote.remoteservices.ITT016RemoteServicesMSASLegacy;

import lombok.Setter;

public class TT016RemoteServerSAS extends AbstractRemoteServer implements IBisCredentialProvider {

	private static final Logger logger = LoggerFactory.getLogger(TT016RemoteServerSAS.class);

	private static final String BIS_USER_NAME = "TRAINER";

	private ITT016RemoteServicesMSASLegacy remoteServices;
	private AbstractMasterConnector connector;
	private IStorage storage;

	private SkuConverter skuConverter;
	private final EncryptionConverter encryptionConverter = new EncryptionConverter();
	private final ProductionDataConverter productionDataConverter = new ProductionDataConverter();
	
	@Setter
	protected ProductionParameters productionParameters;

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
		/** No Encorder for SAS */
	}


	@Override
	public void downloadEncoder(int batchesQuantity, com.sicpa.standard.sasscl.model.CodeType codeType, int year)
			throws RemoteServerException {
		/** No Encorder for SAS */
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
			} else if (products.getProductStatus().equals(TT016ProductStatus.AGING)) {
				sendExportAgingData(products);
			} else if (products.getProductStatus().equals(ProductStatus.MAINTENANCE)) {
				sendMaintenanceData(products);
			} else if (products.getProductStatus().equals(ProductStatus.REFEED)) {
				sendRefeedData(products);
			} else if(products.getProductStatus().equals(TT016ProductStatus.REFEED_NO_INK)){
				sendEjectedData(products);
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
	
	private void registerSendDataSystemEvent(PackagedProducts products) {
		int productCount = products.getProducts().size();
		MonitoringService
				.addSystemEvent(new BasicSystemEvent(LAST_SENT_TO_REMOTE_SERVER, String.valueOf(productCount)));
	}

	private void sendAuthenticatedData(PackagedProducts products) throws InternalException {
		ActivationSessionDTO data = productionDataConverter.convertAuthenticatedSAS(products,
				remoteServices.getSubsystemId());
		remoteServices.sendDomesticProduction(data);
	}

	private void sendRefeedData(PackagedProducts products) throws InternalException {
		ExportSessionDTO data = productionDataConverter.convertExport(products, remoteServices.getSubsystemId());
		remoteServices.sendRefeedProduction(data);
	}

	private void sendEjectedData(PackagedProducts products) throws InternalException {
		IEjectionDTO data = productionDataConverter.convertEjection(products, remoteServices.getSubsystemId(), productionParameters.getProductionMode());
		remoteServices.sendEjectedProduction(data);
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
	
	private void sendExportAgingData(PackagedProducts products) throws InternalException {
		AgedWineSessionDTO data = productionDataConverter.convertExportAging(products, remoteServices.getSubsystemId());
		remoteServices.sendExportAgingProduction(data);
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

	public void setRemoteServices(ITT016RemoteServicesMSASLegacy remoteServices) {
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
}
