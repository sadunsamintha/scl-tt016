package com.sicpa.standard.sasscl.sicpadata.reader;

import java.io.Serializable;

import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.sicpadata.CryptographyException;

/**
 * 
 * Interface for Authenticator. This interface is used within the project to be implemented by different authenticator
 * 
 * @author YYang
 * 
 * 
 */
public interface IAuthenticator extends Serializable {

	/**
	 * decode encrypted code
	 * 
	 * @param mode
	 *            - pass in null if it is not needed
	 * @param encryptedCode
	 *            - encrypted code in String
	 * @return - instance of IDecodedResult
	 * @throws CryptographyException
	 *             - exception thrown when authenticator failed to decode
	 */
	IDecodedResult decode(String mode, String encryptedCode) throws CryptographyException;
	
	/**
	 * decode encrypted code
	 * 
	 * @param mode
	 *            - pass in null if it is not needed
	 * @param encryptedCode
	 *            - encrypted code in String
	 * @param codeType
	 *            - Code Type
	 * @return - instance of IDecodedResult
	 * @throws CryptographyException
	 *             - exception thrown when authenticator failed to decode
	 */
	IDecodedResult decode(String mode, String encryptedCode, CodeType codeType) throws CryptographyException;

}
