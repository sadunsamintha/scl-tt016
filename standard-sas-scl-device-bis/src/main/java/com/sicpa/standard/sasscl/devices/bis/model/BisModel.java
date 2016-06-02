package com.sicpa.standard.sasscl.devices.bis.model;

import com.sicpa.standard.sasscl.devices.bis.IBisModel;

public class BisModel implements IBisModel {

	private String address;
	private int port;
	private int recognitionResultRequestInterval;
	private int connectionLifeCheckInterval;
	private int unknownSkuId = 9999;
	private String unknownSkuDescription = "UNKNOWN";
	private boolean displayAlertMessage;

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
}
