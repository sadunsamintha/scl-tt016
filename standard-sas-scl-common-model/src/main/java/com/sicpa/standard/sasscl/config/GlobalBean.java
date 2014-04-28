package com.sicpa.standard.sasscl.config;

import java.io.Serializable;

/**
 * Global configuration bean.
 */
public class GlobalBean implements Serializable {

	protected static final long serialVersionUID = 1L;

	/**
	 * Language of the application
	 */
	protected String language;

	/**
	 * the number of days during wich production data are kept after being sent to the remote server
	 */
	protected int cleanUpSendDataThreshold_day;

	protected int productionSendBatchSize;

	protected boolean useBarcodeReader;

	protected boolean displaySimulatorGui;

	protected int remoteServerMaxDownTime_day;

	/**
	 * Number of times to serialize production data again after getting serialization exception before stopping
	 * production and sending data to remote server
	 */
	protected int productionDataSerializationErrorThreshold;

	// timeout in seconds
	protected int remoteServerTimeoutCall_sec;

	protected long lineId;
	protected long subsystemId;

	protected String sicpadataPassword;

	protected GlobalAlertModel alertModel;
	
	protected String countryCode;

	public long getLineId() {
		return lineId == 0 ? subsystemId : lineId;
	}

	public void setLineId(long lineId) {
		this.lineId = lineId;
	}

	public void setRemoteServerMaxDownTime_day(final int remoteServerMaxDownTime) {
		this.remoteServerMaxDownTime_day = remoteServerMaxDownTime;
	}

	public int getRemoteServerMaxDownTime_day() {
		return this.remoteServerMaxDownTime_day;
	}

	public void setLanguage(final String language) {
		this.language = language;
	}

	public String getLanguage() {
		return this.language;
	}

	public int getCleanUpSendDataThreshold_day() {
		return this.cleanUpSendDataThreshold_day;
	}

	public void setCleanUpSendDataThreshold_day(final int cleanUpSendDataThreahold) {
		this.cleanUpSendDataThreshold_day = cleanUpSendDataThreahold;
	}

	public int getProductionSendBatchSize() {
		return this.productionSendBatchSize;
	}

	public void setProductionSendBatchSize(final int productionSaveBatchSize) {
		this.productionSendBatchSize = productionSaveBatchSize;
	}

	public void setUseBarcodeReader(final boolean useBarcodeReader) {
		this.useBarcodeReader = useBarcodeReader;
	}

	public boolean isUseBarcodeReader() {
		return this.useBarcodeReader;
	}

	public boolean isDisplaySimulatorGui() {
		return this.displaySimulatorGui;
	}

	public void setDisplaySimulatorGui(final boolean displaySimulatorGui) {
		this.displaySimulatorGui = displaySimulatorGui;
	}

	public int getProductionDataSerializationErrorThreshold() {
		return this.productionDataSerializationErrorThreshold;
	}

	public void setProductionDataSerializationErrorThreshold(final int productionDataSerializationErrorThreshold) {
		this.productionDataSerializationErrorThreshold = productionDataSerializationErrorThreshold;
	}

	public void setRemoteServerTimeoutCall_sec(int remoteServerTimeoutCall_sec) {
		this.remoteServerTimeoutCall_sec = remoteServerTimeoutCall_sec;
	}

	public int getRemoteServerTimeoutCall_sec() {
		return this.remoteServerTimeoutCall_sec;
	}

	public void setSubsystemId(long subsystemid) {
		this.subsystemId = subsystemid;
	}

	public long getSubsystemId() {
		return subsystemId;
	}

	public String getSicpadataPassword() {
		return sicpadataPassword;
	}

	public void setSicpadataPassword(String sicpadataPassword) {
		this.sicpadataPassword = sicpadataPassword;
	}

	public void setAlertModel(GlobalAlertModel alertModel) {
		this.alertModel = alertModel;
	}

	public GlobalAlertModel getAlertModel() {
		return alertModel;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
}
