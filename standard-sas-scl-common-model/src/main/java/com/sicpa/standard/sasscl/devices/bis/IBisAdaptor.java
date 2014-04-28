package com.sicpa.standard.sasscl.devices.bis;

import java.util.List;

import com.sicpa.standard.sasscl.devices.IStartableDevice;

public interface IBisAdaptor extends IStartableDevice {
	
	/**
	 * Request BIS to start reading
	 * 
	 * @throws BisAdaptorException
	 */
	@Override
	void start() throws BisAdaptorException;

	/**
	 * Request BIS to stop reading
	 */
	@Override
	void stop()throws BisAdaptorException;

	void setBisListeners(List<IBisListener> listeners);

}
