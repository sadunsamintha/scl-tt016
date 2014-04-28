package com.sicpa.standard.sasscl.devices.bis.model;

import com.sicpa.standard.sasscl.devices.bis.IBisModel;

public class BisModel implements IBisModel {

	protected String address;
	protected int port;
	protected int recognitionResultRequestInterval;
	protected int connectionLifeCheckInterval;
	// threshold use to check percentage of unread sku by window size
	protected int unreadWindowThreshold;
	protected int unreadWindowSize;
	// threshold use to check consecutive unknown sku
	protected int unknownSkuThreshold;
	protected int unknownSkuId = 9999;
	protected String unknownSkuDescription = "UNKNOWN";
	protected boolean displayAlertMessage;

	@Override
	public String getAddress() {
		return address;
	}

	@Override
	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public int getPort() {
		return port;
	}

	@Override
	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public int getRecognitionResultRequestInterval() {
		return recognitionResultRequestInterval;
	}

	@Override
	public void setRecognitionResultRequestInterval(int recognitionResultRequestInterval) {
		this.recognitionResultRequestInterval = recognitionResultRequestInterval;
	}

	@Override
	public int getConnectionLifeCheckInterval() {
		return connectionLifeCheckInterval;
	}

	@Override
	public void setConnectionLifeCheckInterval(int connectionLifeCheckInterval) {
		this.connectionLifeCheckInterval = connectionLifeCheckInterval;
	}

	@Override
	public int getUnknownSkuThreshold() {
		return unknownSkuThreshold;
	}

	@Override
	public void setUnknownSkuThreshold(int unknownSkuThreshold) {
		this.unknownSkuThreshold = unknownSkuThreshold;
	}

	@Override
	public int getUnknownSkuId() {
		return unknownSkuId;
	}

	@Override
	public void setUnknownSkuId(int unknownSkuId) {
		this.unknownSkuId = unknownSkuId;
	}

	@Override
	public String getUnknownSkuDescription() {
		return unknownSkuDescription;
	}

	@Override
	public void setUnknownSkuDescription(String unknownSkuDescription) {
		this.unknownSkuDescription = unknownSkuDescription;
	}

	public boolean isDisplayAlertMessage() {
		return displayAlertMessage;
	}

	public void setDisplayAlertMessage(boolean displayAlertMessage) {
		this.displayAlertMessage = displayAlertMessage;
	}

	public int getUnreadWindowThreshold() {
		return unreadWindowThreshold;
	}

	public void setUnreadWindowThreshold(int unreadWindowThreshold) {
		this.unreadWindowThreshold = unreadWindowThreshold;
	}

	public int getUnreadWindowSize() {
		return unreadWindowSize;
	}

	public void setUnreadWindowSize(int unreadWindowSize) {
		this.unreadWindowSize = unreadWindowSize;
	}

}
