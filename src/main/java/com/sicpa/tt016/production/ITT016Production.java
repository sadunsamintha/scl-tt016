package com.sicpa.tt016.production;

import com.sicpa.standard.sasscl.business.production.IProduction;

public interface ITT016Production extends IProduction {

	/**
	 * This operation retrieves all the sku gross net data that has not been sent to the remote server (if any),
	 * and sends it to the remote server.
	 */
	void sendAllSkuGrossNetData();
}
