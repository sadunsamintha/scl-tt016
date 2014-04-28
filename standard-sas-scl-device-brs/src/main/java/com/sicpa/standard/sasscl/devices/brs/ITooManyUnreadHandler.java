package com.sicpa.standard.sasscl.devices.brs;

public interface ITooManyUnreadHandler {

	void addRead(int count);

	void addUnread(int count);

	boolean isThresholdReached();
}
