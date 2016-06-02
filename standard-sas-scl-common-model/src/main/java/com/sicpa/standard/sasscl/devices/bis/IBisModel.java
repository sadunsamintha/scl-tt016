package com.sicpa.standard.sasscl.devices.bis;

public interface IBisModel {

	String getAddress();

	void setAddress(String address);

	int getPort();

	void setPort(int port);

	int getRecognitionResultRequestInterval();

	void setRecognitionResultRequestInterval(int recognitionResultRequestInterval);

	int getConnectionLifeCheckInterval();

	void setConnectionLifeCheckInterval(int connectionLifeCheckInterval);

	int getUnknownSkuId();

	void setUnknownSkuId(int unknownSkuId);

	String getUnknownSkuDescription();

	void setUnknownSkuDescription(String unknownSkuDescription);

	boolean isDisplayAlertMessage();

	void setDisplayAlertMessage(boolean displayAlertMessage);

}