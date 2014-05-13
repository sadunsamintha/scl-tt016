package com.sicpa.standard.sasscl.sicpadata.generator;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.sicpa.standard.printer.xcode.ExtendedCode;
import com.sicpa.standard.sasscl.sicpadata.CryptographyException;

/**
 * 
 * Interface for encoder used in the project.
 * 
 * 
 * @author YYang
 * 
 */
public interface IEncoder extends Serializable {

	/**
	 * Encoder identifier
	 * 
	 * can be use as primary key in storage
	 * 
	 * @return
	 */
	long getId();
	
	long getBatchId();

	/**
	 * 
	 * Get a number of encrypted code and return it as a list of String
	 * 
	 * If the encoder does not have enough code, all the available codes will be returned
	 * 
	 * @param numberCodes
	 *            number of encrypted code to request
	 * 
	 * 
	 * @return list of encrypted code
	 * 
	 * @throws CryptographyException
	 *             thrown exception if the encoder is empty
	 */
	List<String> getEncryptedCodes(long numberOfCodes) throws CryptographyException;

	/**
	 * Check if the encoder is empty. If the encoder is empty, the encoder cannot produce anymore encrypted code by
	 * calling getEncryptedCode(). If the "getEncryptedCode()" is called again if this method return true,
	 * EncoderEmptyException will be thrown
	 * 
	 * Not every implementation supports this method. throw UnsupportedOperationException if this method is not
	 * supported
	 * 
	 * @return true if encoder is empty, false otherwise.
	 */
	boolean isEncoderEmpty();

	int getYear();

	long getSubsystemId();

	int getCodeTypeId();

	/**
	 * 
	 * @return the date when the first code was generated
	 */
	Date getFirstCodeDate();

	/**
	 * 
	 * @return the date when the last code was generated
	 */
	Date getLastCodeDate();

	/**
	 * @return the date when the encoder was received on the client side
	 */
	Date getOnClientDate();
	
	void setOnClientDate(Date onClientDate);

	List<ExtendedCode> getExtendedCodes(long numberCodes)
			throws CryptographyException;

}
