package com.sicpa.tt016.scl.remote;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.sasscl.common.storage.IStorage;
import com.sicpa.standard.sasscl.devices.DeviceException;
import com.sicpa.standard.sasscl.devices.IDeviceStatusListener;
import com.sicpa.standard.sasscl.devices.remote.AbstractRemoteServer;
import com.sicpa.standard.sasscl.devices.remote.GlobalMonitoringToolInfo;
import com.sicpa.standard.sasscl.devices.remote.RemoteServerException;
import com.sicpa.standard.sasscl.devices.remote.connector.AbstractMasterConnector;
import com.sicpa.standard.sasscl.model.EncoderInfo;
import com.sicpa.standard.sasscl.model.PackagedProducts;
import com.sicpa.standard.sasscl.model.ProductStatus;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionParameterRootNode;
import com.sicpa.standard.sasscl.sicpadata.generator.IEncoder;
import com.sicpa.standard.sasscl.sicpadata.reader.IAuthenticator;
import com.sicpa.tt016.common.dto.CodingActivationSessionDTO;
import com.sicpa.tt016.common.dto.EncoderInfoDTO;
import com.sicpa.tt016.common.dto.EncoderInfoResultDTO;
import com.sicpa.tt016.common.dto.EncoderInfoResultDTO.InfoResult;
import com.sicpa.tt016.common.dto.EncoderSclDTO;
import com.sicpa.tt016.common.dto.ExportSessionDTO;
import com.sicpa.tt016.common.dto.IEjectionDTO;
import com.sicpa.tt016.common.dto.MaintenanceSessionDTO;
import com.sicpa.tt016.master.scl.exceptions.InternalException;
import com.sicpa.tt016.scl.remote.assembler.EncryptionConverter;
import com.sicpa.tt016.scl.remote.assembler.ProductionDataConverter;
import com.sicpa.tt016.scl.remote.assembler.SkuConverter;
import com.sicpa.tt016.scl.remote.remoteservices.ITT016RemoteServices;

public class TT016RemoteServer extends AbstractRemoteServer {

	private static final Logger logger = LoggerFactory.getLogger(TT016RemoteServer.class);
	// http://psdwiki.sicpa-net.ads/pages/viewpage.action?spaceKey=morocco&title=Development+and+Integration+Servers

	private ITT016RemoteServices remoteServices;
	private AbstractMasterConnector connector;
	private IStorage storage;

	private SkuConverter skuConverter;
	private final EncryptionConverter encryptionConverter = new EncryptionConverter();
	private final ProductionDataConverter productionDataConverter = new ProductionDataConverter();

	public TT016RemoteServer() {
	}
	
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

	public boolean isRefeedEnabled() {
		try {
			return remoteServices.isRefeedEnabled();
		} catch (InternalException e) {
			logger.error("Error getting the isRefeed from the DMS." + e.getMessage());
			return false;
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
			}
		} catch (InternalException e) {
			throw new RemoteServerException(e);
		}
	}

	private void storeEncoder(IEncoder encoder, int year) {
		storage.saveEncoders(year, encoder);
		storage.confirmEncoder(encoder.getId());
	}

	@Override
	public void sendProductionData(PackagedProducts products) throws RemoteServerException {
		try {
			if (products.getProductStatus() == ProductStatus.EXPORT) {
				sendExportData(products);
			} else if (products.getProductStatus() == ProductStatus.MAINTENANCE) {
				sendMaintenanceData(products);
			} else if (products.getProductStatus() == ProductStatus.UNREAD) {
				sendEjectedData(products);
			} else if (products.getProductStatus() == ProductStatus.AUTHENTICATED) {
				sendAuthenticatedData(products);
			} else if (products.getProductStatus() == ProductStatus.REFEED) {
				sendRefeedData(products);
			}
		} catch (Exception e) {
			throw new RemoteServerException("", e);
		}
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

	private void sendMaintenanceData(PackagedProducts products) throws InternalException {
		MaintenanceSessionDTO data = productionDataConverter.convertMaintenance(products,
				remoteServices.getSubsystemId());
		remoteServices.sendMaintenanceProduction(data);
	}

	private void sendExportData(PackagedProducts products) throws InternalException {
		ExportSessionDTO data = productionDataConverter.convertExport(products, remoteServices.getSubsystemId());
		remoteServices.sendExportProduction(data);
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
}