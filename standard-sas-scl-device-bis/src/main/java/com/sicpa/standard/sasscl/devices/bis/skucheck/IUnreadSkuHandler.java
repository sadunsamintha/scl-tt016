package com.sicpa.standard.sasscl.devices.bis.skucheck;

public interface IUnreadSkuHandler {

	void addRead();

	void addUnread();

	boolean isThresholdReached();
	
}
