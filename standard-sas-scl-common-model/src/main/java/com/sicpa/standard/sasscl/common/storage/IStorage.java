package com.sicpa.standard.sasscl.common.storage;

import java.io.Serializable;
import java.util.List;

import com.sicpa.standard.client.common.storage.StorageException;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.model.EncoderInfo;
import com.sicpa.standard.sasscl.model.PackagedProducts;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.statistics.StatisticsValues;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionParameterRootNode;
import com.sicpa.standard.sasscl.sicpadata.generator.IEncoder;
import com.sicpa.standard.sasscl.sicpadata.reader.IAuthenticator;

/**
 * 
 * Implement this interface to create a new way of storing objects
 * 
 * All the getter methods will return a new instance every time it is called
 * 
 * @author DIelsch
 * 
 * 
 */
public interface IStorage {

	/**
	 * Serialize authenticator to be used in later stage
	 * 
	 * @param auth
	 */
	void saveAuthenticator(final IAuthenticator auth);

	/**
	 * Retrieve authenticator from the storage
	 * 
	 * @return IAuthenticator
	 */
	IAuthenticator getAuthenticator();

	/**
	 * Save the passed in encoder as the current encoder.
	 * 
	 * @param encoder
	 * @param codeType
	 *            Associated code type of the encoder, to be able to retrieve the encoder by code type
	 */
	void saveCurrentEncoder(IEncoder encoder);

	/**
	 * Save an array of encoders in the storage for a given code type and year
	 * 
	 * @param codeType
	 * @param year
	 * @param encoders
	 */
	void saveEncoders(int year, IEncoder... encoders);

	/**
	 * Get the current encoder for a given code type
	 * 
	 * @return the current encoder
	 */
	IEncoder getCurrentEncoder(CodeType codeType);

	/**
	 * Get the next encoder, and make it the current encoder
	 * 
	 * @return the new current encoder
	 */
	IEncoder useNextEncoder(CodeType codeType);

	/**
	 * @return the list of encoder that are in the pending folder
	 */
	List<IEncoder> getPendingEncoders();

	/**
	 * move the encoder from pending to confirm
	 */
	void confirmEncoder(long id);

	void removePendingEncoder(long id);

	/**
	 * info about internal/encoders/finished/pending has been sent, so move those encoders to
	 * internal/encoders/finished/confirmed
	 */
	void notifyEncodersInfoSent(List<EncoderInfo> encoderInfos);

	/**
	 * Save the production parameters in the storage after downloading them from the remote server.
	 * 
	 * @param node
	 */
	void saveProductionParameters(final ProductionParameterRootNode node);

	/**
	 * Retrieve production parameters from storage.
	 * 
	 * @return ProductionParameterRootNode
	 */
	ProductionParameterRootNode getProductionParameters();

	/**
	 * Save the selected production parameter in local storage
	 * 
	 * @param param
	 */
	void saveSelectedProductionParamters(final ProductionParameters param);

	/**
	 * retrieve the selected production parameter from storage.
	 * 
	 * @return ProductionParameters
	 */
	ProductionParameters getSelectedProductionParameters();

	/**
	 * save statistics values in the storage.
	 * 
	 * @param stats
	 */
	void saveStatistics(final StatisticsValues stats);

	/**
	 * Get statistic values from local storage
	 * 
	 * @return StatisticsValues
	 */
	StatisticsValues getStatistics();

	/**
	 * Serialize and save the given products
	 */
	void saveProduction(final Product[] products) throws StorageException;

	/**
	 * return a collection of product that has been saved
	 * 
	 * @return PackagedProducts
	 */
	PackagedProducts getABatchOfProducts();

	/**
	 * 
	 * @return the number of batch to be sent to the remote server
	 */
	int getBatchOfProductsCount();

	/**
	 * notify that the production has been send to the remote server, the production files are moved to the send
	 * directory
	 */
	void notifyDataSentToRemoteServer();

	/**
	 * notify to move the production data to the error folder because there was trouble sending the production data to
	 * the remote server to the send directory
	 */
	void notifyDataErrorSendingToRemoteServer();

	/**
	 * remove sent production file that are too old
	 */
	void cleanUpOldSentProduction();

	/**
	 * 
	 * Save a serializable object in storage
	 * 
	 * @param object
	 *            the object to save
	 * @param id
	 *            an id that can be used by the implementation, for example using id as File
	 * 
	 * @throws StorageException
	 *             throw exception if fail to save object
	 */
	void save(Serializable object, String id) throws StorageException;

	/**
	 * save a serializable object in quarantine folder
	 * 
	 * @param object
	 *            the object to save
	 * @param id
	 *            an id that can be used by the implementation, for example using id as File
	 */
	void saveToQuarantine(Serializable object, String id, QuarantineReason reason);

	/**
	 * 
	 * remove an object from storage
	 * 
	 * @param id
	 *            to locate object
	 * 
	 * @throws StorageException
	 *             throw exception if fail to remove object by the given id
	 */
	void remove(String id) throws StorageException;

	/**
	 * @param id
	 *            an id that can be used by the implementation, for example using id as File
	 */
	Object load(String id) throws StorageException;

	/**
	 * 
	 * @return the number of encoder in storage, does not count the current encoder
	 */
	int getAvailableNumberOfEncoders(CodeType codeType, int year);

	/**
	 * 
	 * package production data into a more organized structure
	 * 
	 * @param batchSize
	 *            the number of product that should contain package of production
	 */
	void packageProduction(int batchSize);

	/**
	 * 
	 * @return some information about the current state of the storage for monitoring purpose
	 */
	String getStorageInfo();

	/**
	 * 
	 * @return a list containing a "EncoderInfo" for each encoder on the client (buffered/current/finished)
	 */
	List<EncoderInfo> getAllEndodersInfo();
	
	/**
	 * move a specific encoder to the quarantine
	 * @param id
	 */
	void quarantineEncoder(long id);

}
