package com.sicpa.standard.sasscl.controller.scheduling;

import java.util.Calendar;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.sasscl.common.storage.IStorage;
import com.sicpa.standard.sasscl.config.GlobalBean;
import com.sicpa.standard.sasscl.config.GlobalConfigSCL;
import com.sicpa.standard.sasscl.devices.remote.IRemoteServer;
import com.sicpa.standard.sasscl.devices.remote.RemoteServerException;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.provider.impl.AuthenticatorProvider;
import com.sicpa.standard.sasscl.provider.impl.SkuListProvider;

public class RemoteServerScheduledJobsSCL extends RemoteServerScheduledJobs {

	public RemoteServerScheduledJobsSCL(GlobalBean globalConfig, IStorage storage, IRemoteServer remoteServer,
			SkuListProvider productionParametersProvider, AuthenticatorProvider authenticatorProvider) {
		super(globalConfig, storage, remoteServer, productionParametersProvider, authenticatorProvider);
	}

	private static final Logger logger = LoggerFactory.getLogger(RemoteServerScheduledJobsSCL.class);

	/**
	 * Download encoders by year and code type from the remote server and save them in the local storage.
	 */
	public synchronized void getEncodersFromRemoteServer() {

		logger.info("Executing job: Trying to download encoders");
		if (remoteServer.isConnected()) {
			try {

				Calendar calendar = Calendar.getInstance();
				int year = calendar.get(Calendar.YEAR);
				getEncodersFromRemoteServer(year);
			} catch (Exception e) {
				logger.error("Failed to get new encoders", e);
			}
		}
	}

	protected void getEncodersFromRemoteServer(final int year) throws RemoteServerException {
		Set<CodeType> codeTypes = skuListProvider.getAvailableCodeTypes();
		for (CodeType codeType : codeTypes) {
			if (codeType != null) {
				if (storage.getAvailableNumberOfEncoders(codeType, year) <= getGlobalConfig().getMinEncodersThreshold()) {
					remoteServer.downloadEncoder(getGlobalConfig().getRequestNumberEncoders(), codeType, year);
				}
			}
		}
	}

	public GlobalConfigSCL getGlobalConfig() {
		return (GlobalConfigSCL) globalConfig;
	}

	@Override
	public void executeInitialTasks() {
		super.executeInitialTasks();
		getEncodersFromRemoteServer();
	}
}
