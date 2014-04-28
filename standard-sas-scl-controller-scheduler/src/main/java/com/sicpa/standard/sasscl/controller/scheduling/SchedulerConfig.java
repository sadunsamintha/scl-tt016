package com.sicpa.standard.sasscl.controller.scheduling;

import com.sicpa.standard.client.common.utils.ConfigUtils;

public class SchedulerConfig {

	public SchedulerConfig(final String file) {
		setDefaultValues();
		ConfigUtils.loadCopySave(this, file);
	}

	public SchedulerConfig() {
		setDefaultValues();
	}

	// activated product serialization timer
	protected int productionSerializationTimer_sec;

	// send production data to remote server timer
	protected int productionSendTimer_sec;

	// get authenticator from remote server timer
	protected int getAuthenticathorTimer_sec;

	// get production parameters from remote server timer
	protected int getProductionParametersTimer_sec;

	// timer for creating batch of product
	protected int packagedProductTimer_sec;

	// timer for removing old production data that has been already send to the master
	protected int storageProductionCleanUp_sec;

	// timer for getting the languages file from the remote server
	protected int getLanguageFilesTimer_sec;

	// timer for check remote server maximum downtime
	protected int maxRemoteServerDownTimeTimer_sec;

	// timer for save statistics
	protected int saveStatisticsTimer_sec;

	// timer for monitoring save incremental statistics
	protected int monitoringSaveIncrementalStatisticsTimer_sec;

	protected int globalMonitoringToolSendInfoTimer_sec;

	protected void setDefaultValues() {
		// time unit is in second
		setProductionSerializationTimer_sec(5);// 5 sec
		setProductionSendTimer_sec(10);// 10 sec
		setPackagedProductTimer_sec(10);// 10 sec
		setGetProductionParametersTimer_sec(7200);// 2hours
		setGetAuthenticathorTimer_sec(7200);// 2hours
		setStorageProductionCleanUp_sec(7200);// 2hours
		setGetLanguageFilesTimer_sec(7200);// 2hours
		setMaxRemoteServerDownTimeTimer_sec(600);// 10 min
		setSaveStatisticsTimer_sec(60);// 1 minute
		setMonitoringSaveIncrementalStatisticsTimer_sec(300);// 5 min
		setGlobalMonitoringToolSendInfoTimer_sec(300);// 5 min
	}

	public void setMaxRemoteServerDownTimeTimer_sec(final int maxRemoteServerDownTimeTimer) {
		this.maxRemoteServerDownTimeTimer_sec = maxRemoteServerDownTimeTimer;
	}

	public int getMaxRemoteServerDownTimeTimer_sec() {
		return this.maxRemoteServerDownTimeTimer_sec;
	}

	public void setProductionSerializationTimer_sec(final int productionSerializationTimer) {
		this.productionSerializationTimer_sec = productionSerializationTimer;
	}

	public int getProductionSerializationTimer_sec() {
		return this.productionSerializationTimer_sec;
	}

	public int getProductionSendTimer_sec() {
		return this.productionSendTimer_sec;
	}

	public void setProductionSendTimer_sec(final int productionSendTimer) {
		this.productionSendTimer_sec = productionSendTimer;
	}

	public int getGetAuthenticathorTimer_sec() {
		return this.getAuthenticathorTimer_sec;
	}

	public void setGetAuthenticathorTimer_sec(final int getAuthenticathorTimer) {
		this.getAuthenticathorTimer_sec = getAuthenticathorTimer;
	}

	public int getGetProductionParametersTimer_sec() {
		return this.getProductionParametersTimer_sec;
	}

	public void setGetProductionParametersTimer_sec(final int getProductionParametersTimer) {
		this.getProductionParametersTimer_sec = getProductionParametersTimer;
	}

	public void setPackagedProductTimer_sec(final int packagedProductTimer) {
		this.packagedProductTimer_sec = packagedProductTimer;
	}

	public int getPackagedProductTimer_sec() {
		return this.packagedProductTimer_sec;
	}

	public int getStorageProductionCleanUp_sec() {
		return this.storageProductionCleanUp_sec;
	}

	public void setStorageProductionCleanUp_sec(final int storageProductionCleanUp) {
		this.storageProductionCleanUp_sec = storageProductionCleanUp;
	}

	public int getGetLanguageFilesTimer_sec() {
		return this.getLanguageFilesTimer_sec;
	}

	public void setGetLanguageFilesTimer_sec(final int getLanguageFilesTimer) {
		this.getLanguageFilesTimer_sec = getLanguageFilesTimer;
	}

	public int getSaveStatisticsTimer_sec() {
		return this.saveStatisticsTimer_sec;
	}

	public void setSaveStatisticsTimer_sec(final int saveStatisticsTimer) {
		this.saveStatisticsTimer_sec = saveStatisticsTimer;
	}

	public int getMonitoringSaveIncrementalStatisticsTimer_sec() {
		return monitoringSaveIncrementalStatisticsTimer_sec;
	}

	public void setMonitoringSaveIncrementalStatisticsTimer_sec(int monitoringSaveIncrementalStatisticsTimer) {
		this.monitoringSaveIncrementalStatisticsTimer_sec = monitoringSaveIncrementalStatisticsTimer;
	}

	public void setGlobalMonitoringToolSendInfoTimer_sec(int globalMonitoringToolSendInfoTimer_sec) {
		this.globalMonitoringToolSendInfoTimer_sec = globalMonitoringToolSendInfoTimer_sec;
	}

	public int getGlobalMonitoringToolSendInfoTimer_sec() {
		return globalMonitoringToolSendInfoTimer_sec;
	}
}
