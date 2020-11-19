package com.sicpa.tt016.devices.remote;

import com.sicpa.standard.sasscl.devices.remote.IRemoteServer;
import com.sicpa.tt016.common.dto.SkuGrossNetProductCounterDTO;
import com.sicpa.tt016.master.scl.exceptions.InternalException;

public interface ITT016RemoteServer extends IRemoteServer {

	/**
	 * 
	 * send sku gross net product count to remote server
	 * 
	 * @param skuGrossNetProductArray
	 *            List of sku gross net product count info to be sent to remote server
	 * 
	 * @throws InternalException
	 *             throw exception if the sending fails
	 */
	void sendSkuGrossNetProductCounter(SkuGrossNetProductCounterDTO[] skuGrossNetProductArray) throws InternalException;
}
