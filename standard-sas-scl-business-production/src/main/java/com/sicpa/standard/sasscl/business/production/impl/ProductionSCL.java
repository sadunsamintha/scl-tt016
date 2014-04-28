package com.sicpa.standard.sasscl.business.production.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.sasscl.common.storage.IStorage;
import com.sicpa.standard.sasscl.config.GlobalBean;
import com.sicpa.standard.sasscl.devices.remote.IRemoteServer;
import com.sicpa.standard.sasscl.model.EncoderInfo;

public class ProductionSCL extends Production {
	
	private static final Logger logger = LoggerFactory.getLogger(Production.class);

	public ProductionSCL(GlobalBean globalConfig, IStorage storage,	IRemoteServer remoteServer) {
		super(globalConfig, storage, remoteServer);
	}
	
	@Override
	public void sendAllProductionData() {

		if (!remoteServer.isConnected()) {
			return;
		}

		synchronized (sendProductionLock) {
			logger.debug("Send production");
			sendAllEncoderInfo();
			sendAllBatchOfProducts();
		}
	}


	protected void sendAllEncoderInfo() {
		try {
			List<EncoderInfo> encoderInfos = storage.getAllEndodersInfo();
			remoteServer.sendEncoderInfos(encoderInfos);
			storage.notifyEncodersInfoSent(encoderInfos);
		} catch (Exception e) {
			logger.error("error sending encoder infos", e);
		}
	}

}
