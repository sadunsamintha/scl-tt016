package com.sicpa.standard.sasscl.controller.scheduling;

import java.util.Calendar;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.sasscl.common.storage.IStorage;
import com.sicpa.standard.sasscl.devices.remote.IRemoteServer;
import com.sicpa.standard.sasscl.devices.remote.RemoteServerException;
import com.sicpa.standard.sasscl.filter.CodeTypeFilterFactory;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.provider.impl.AuthenticatorProvider;
import com.sicpa.standard.sasscl.provider.impl.SkuListProvider;

public class RemoteServerScheduledJobsSCL extends RemoteServerScheduledJobs {

	private int requestNumberEncoders;
	private int minEncodersThreshold;
	private CodeTypeFilterFactory codeTypeFilterFactory;

	public RemoteServerScheduledJobsSCL(IStorage storage, IRemoteServer remoteServer,
			SkuListProvider productionParametersProvider, AuthenticatorProvider authenticatorProvider) {
		super(storage, remoteServer, productionParametersProvider, authenticatorProvider);
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

	private void getEncodersFromRemoteServer(final int year) throws RemoteServerException {
		Set<CodeType> codeTypes = skuListProvider.getAvailableCodeTypes();

		// Filter
		Set<CodeType> codeTypesFilter = codeTypes.stream().filter(codeTypeFilterFactory.getFilter())
				.collect(Collectors.toSet());

		for (CodeType codeType : codeTypesFilter) {
			if (codeType != null) {
				if (shouldRequestEncoder(codeType, year)) {
					try {
						remoteServer.downloadEncoder(requestNumberEncoders, codeType, year);
					} catch (Exception e) {
						logger.error("Failed to download encoder for code type {}", codeType, e);
					}
				}
			}
		}
	}

	private boolean shouldRequestEncoder(CodeType codeType, int year) {
		return storage.getAvailableNumberOfEncoders(codeType, year) <= minEncodersThreshold;
	}

	@Override
	public void executeInitialTasks() {
		super.executeInitialTasks();
		getEncodersFromRemoteServer();
	}

	public void setRequestNumberEncoders(int requestNumberEncoders) {
		this.requestNumberEncoders = requestNumberEncoders;
	}

	public void setMinEncodersThreshold(int minEncodersThreshold) {
		this.minEncodersThreshold = minEncodersThreshold;
	}

	public CodeTypeFilterFactory getCodeTypeFilterFactory() {
		return codeTypeFilterFactory;
	}

	public void setCodeTypeFilterFactory(CodeTypeFilterFactory codeTypeFilterFactory) {
		this.codeTypeFilterFactory = codeTypeFilterFactory;
	}
}
