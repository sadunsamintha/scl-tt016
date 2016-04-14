package com.sicpa.standard.sasscl.controller.scheduling;

import java.util.Calendar;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.sasscl.devices.remote.RemoteServerException;
import com.sicpa.standard.sasscl.filter.CodeTypeFilterFactory;
import com.sicpa.standard.sasscl.model.CodeType;

public class RemoteServerScheduledJobsSCL extends RemoteServerScheduledJobs {

	private int requestNumberEncoders;
	private int minEncodersThreshold;
	private CodeTypeFilterFactory codeTypeFilterFactory;

	private static final Logger logger = LoggerFactory.getLogger(RemoteServerScheduledJobsSCL.class);

	/**
	 * Download encoders by year and code type from the remote server and save them in the local storage.
	 */
	public synchronized void getEncodersFromRemoteServer() {

		logger.info("Executing job: Trying to download encoders");
		if (remoteServer.isConnected()) {
			try {
				filterAndRequestEncoders();
			} catch (Exception e) {
				logger.error("Failed to get new encoders", e);
			}
		}
	}

	private int getCurrentYear() {
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		return year;
	}

	private void filterAndRequestEncoders() throws RemoteServerException {
		Set<CodeType> codeTypesFiltered = getCodeTypesLinkedWithEncoder();
		requestEncoders(codeTypesFiltered);
	}

	private Set<CodeType> getCodeTypesLinkedWithEncoder() {
		Set<CodeType> codeTypes = skuListProvider.getAvailableCodeTypes();
		Set<CodeType> codeTypesFiltered = codeTypes.stream().filter(codeTypeFilterFactory.getFilter())
				.collect(Collectors.toSet());
		return codeTypesFiltered;
	}

	private void requestEncoders(Set<CodeType> codeTypes) {
		int year = getCurrentYear();
		for (CodeType codeType : codeTypes) {
			if (shouldRequestEncoder(codeType, year)) {
				try {
					remoteServer.downloadEncoder(requestNumberEncoders, codeType, year);
				} catch (Exception e) {
					logger.error("Failed to download encoder for code type {}", codeType, e);
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
