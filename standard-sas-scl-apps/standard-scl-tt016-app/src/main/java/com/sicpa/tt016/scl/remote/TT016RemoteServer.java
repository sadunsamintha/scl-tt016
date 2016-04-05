/*
 * Author   		: JBarbieri
 * Date     		: 20-Oct-2010
 *
 * Project  		: tt016-spl
 * Package 			: com.sicpa.tt016.spl.devices.remoteServer.comm.ejb
 * File   			: EjbMasterComm.java
 *
 * Revision 		: $Revision: 7913 $
 * Last modified	: $LastChangedDate: 2011-11-22 15:39:44 +0100 (Tue, 22 Nov 2011) $
 * Last modified by	: $LastChangedBy: cdealmeida $
 *
 * Copyright (c) 2010 SICPA Product Security SA, all rights reserved.
 */
package com.sicpa.tt016.scl.remote;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.timeout.Timeout;
import com.sicpa.standard.client.common.timeout.TimeoutLifeCheck;
import com.sicpa.standard.sasscl.devices.DeviceException;
import com.sicpa.standard.sasscl.devices.DeviceStatus;
import com.sicpa.standard.sasscl.devices.remote.AbstractRemoteServer;
import com.sicpa.standard.sasscl.devices.remote.GlobalMonitoringToolInfo;
import com.sicpa.standard.sasscl.devices.remote.RemoteServerException;
import com.sicpa.standard.sasscl.devices.remote.connector.AbstractMasterConnector;
import com.sicpa.standard.sasscl.model.EncoderInfo;
import com.sicpa.standard.sasscl.model.PackagedProducts;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionParameterRootNode;
import com.sicpa.standard.sasscl.sicpadata.generator.IEncoder;
import com.sicpa.standard.sasscl.sicpadata.reader.IAuthenticator;
import com.sicpa.tt016.common.dto.EncoderInfoDTO;
import com.sicpa.tt016.common.dto.EncoderInfoResultDTO;
import com.sicpa.tt016.common.dto.EncoderSclDTO;
import com.sicpa.tt016.master.scl.exceptions.InternalException;
import com.sicpa.tt016.scl.remote.assembler.EncryptionConverter;
import com.sicpa.tt016.scl.remote.assembler.SkuConverter;
import com.sicpa.tt016.scl.remote.remoteservices.IRemoteServices;
import com.sicpa.tt016.scl.skucheck.SkuCheckAssembly;

public class TT016RemoteServer extends AbstractRemoteServer {

	private static final Logger logger = LoggerFactory.getLogger(TT016RemoteServer.class);

	private IRemoteServices remoteServices;
	private AbstractMasterConnector connector;
	private final SkuConverter skuAssembler = new SkuConverter();
	private final EncryptionConverter encryptionConverter = new EncryptionConverter();

	public TT016RemoteServer() {
	}

	public String getBisUserPassword(String user) {
		return remoteServices.getBisUserPassword(user);
	}

	@Override
	@Timeout
	public ProductionParameterRootNode getTreeProductionParameters() throws RemoteServerException {
		if (!isConnected()) {
			return null;
		}
		try {
			return skuAssembler.convert(remoteServices.getSkuList());
		} catch (InternalException e) {
			throw new RemoteServerException(e);
		}
	}

	@Timeout
	public boolean isRefeedEnabled() {
		try {
			return remoteServices.isRefeedEnabled();
		} catch (InternalException e) {
			logger.error("Error getting the isRefeed from the DMS." + e.getMessage());
			return false;
		}
	}

	@Override
	@Timeout
	public IAuthenticator getAuthenticator() throws RemoteServerException {
		if (!isConnected()) {
			return null;
		}
		return encryptionConverter.convert(remoteServices.getDecoder());
	}

	@Override
	@Timeout
	public void sendEncoderInfos(List<EncoderInfo> infos) throws RemoteServerException {

		List<EncoderInfoDTO> dtos = new ArrayList<>();
		for (EncoderInfo i : infos) {
			dtos.add(encryptionConverter.convert(i));
		}
		try {
			EncoderInfoResultDTO res = remoteServices.sendEncoderInfo(dtos);
			// TODO
		} catch (InternalException e) {
			logger.error("", e);
		}
	}

	@Override
	@Timeout
	public void downloadEncoder(int batchesQuantity, com.sicpa.standard.sasscl.model.CodeType codeType, int year)
			throws RemoteServerException {
		try {
			List<EncoderSclDTO> encoders = remoteServices.getRemoteEncoders(batchesQuantity, (int) codeType.getId());
			for (EncoderSclDTO e : encoders) {
				storeEncoder(encryptionConverter.convert(e));
			}
		} catch (InternalException e) {
			throw new RemoteServerException(e);
		}
	}

	private void storeEncoder(IEncoder encoder) {
		// TODO
	}

	@Timeout
	public boolean sendSkuCheckAssembly(SkuCheckAssembly assembly) throws InternalException {
		// TODO remoteServices.sendNonCompliantSession(sessionList);
		return true;
	}

	@Override
	@Timeout
	public void sendProductionData(PackagedProducts products) throws RemoteServerException {
		// TODO
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
	@TimeoutLifeCheck
	public void lifeCheckTick() {
		try {
			remoteServices.isAlive();
			fireDeviceStatusChanged(DeviceStatus.CONNECTED);
		} catch (Exception e) {
			logger.error("", e);
			fireDeviceStatusChanged(DeviceStatus.DISCONNECTED);
		}
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
}