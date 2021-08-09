package com.sicpa.tt016.storage;

import com.sicpa.standard.client.common.storage.StorageException;
import com.sicpa.standard.sasscl.common.storage.IStorage;
import com.sicpa.tt016.common.dto.SkuGrossNetProductCounterDTO;

/**
 * 
 * Implement this interface to create a new way of storing objects for MA SKU Gross Net
 * 
 * All the getter methods will return a new instance every time it is called
 * 
 * @author GGubatan
 * 
 * 
 */
public interface ITT016Storage extends IStorage {

	/**
	 * Save the SKU Gross Net Product Counter in the storage before sending them to the remote server.
	 * 
	 * @param node
	 */
	void saveSkuGrossNet(final SkuGrossNetProductCounterDTO[] skuGrossNetProductList) throws StorageException;
	
	/**
	 * 
	 * @return the number of sku gross net batch to be sent to the remote server
	 */
	int getSkuGrossNetBatchCount();

	/**
	 * return a collection of sku gross net data that has been saved
	 * 
	 * @return SkuGrossNetProductCounterDTO[]
	 */
	SkuGrossNetProductCounterDTO[] getABatchOfSkuGrossNet();
	
	/**
	 * notify that the sku gross net data has been sent to the remote server, the files are moved to the sent
	 * directory
	 */
	void notifySkuGrossNetDataSentToRemoteServer();
	
	/**
	 * notify to move the sku gross net data to the error folder because there was trouble sending to
	 * the remote server from the sent directory
	 */
	void notifySkuGrossNetDataErrorSendingToRemoteServer();

}
