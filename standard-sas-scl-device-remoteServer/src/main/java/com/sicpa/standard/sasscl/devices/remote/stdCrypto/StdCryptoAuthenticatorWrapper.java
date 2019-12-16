package com.sicpa.standard.sasscl.devices.remote.stdCrypto;

import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.model.DecodedCameraCode;
import com.sicpa.standard.sasscl.sicpadata.CryptographyException;
import com.sicpa.standard.sasscl.sicpadata.reader.IAuthenticator;
import com.sicpa.standard.sasscl.sicpadata.reader.IDecodedResult;
import com.sicpa.standard.sicpadata.api.business.IBSicpadataContent;
import com.sicpa.standard.sicpadata.api.business.IBSicpadataReader;

/**
 * 
 * Authenticator that delegates the decode process to standard crypto business authenticator
 * 
 * @author YYang
 * 
 */
public class StdCryptoAuthenticatorWrapper implements IAuthenticator {

	private static final long serialVersionUID = 1L;

	/**
	 * business authenticator
	 */
	protected IBSicpadataReader bAuthenticator;


	protected ICryptoFieldsConfig cryptoFieldsConfig;

	/**
	 * 
	 * @param authenticator
	 *            - instance of business authenticator froms standard crypto business
	 */
	public StdCryptoAuthenticatorWrapper(final IBSicpadataReader authenticator,
			final ICryptoFieldsConfig cryptoFieldsConfig) {
		this.bAuthenticator = authenticator;
		this.cryptoFieldsConfig = cryptoFieldsConfig;
	}

	/**
	 * Convert instance of IBAuthenResult to instance of DecodedCameraCode
	 * 
	 * @param authenResult
	 * @return
	 */
	protected DecodedCameraCode convert(final IBSicpadataContent authenResult) {
		return this.cryptoFieldsConfig.getFields(authenResult);
	}

	/**
	 * 
	 * Decode encrypted code and return instance of DecodedCameraCode
	 * 
	 * @see com.sicpa.standard.sasscl.sicpadata.reader.IAuthenticator#decode(java.lang.String)
	 * 
	 */
	@Override
	public DecodedCameraCode decode(String mode, final String encryptedCode) throws CryptographyException {

		try {

			IBSicpadataContent authenResult = null;

			if (mode == null || mode.trim().isEmpty()) {
				authenResult = this.bAuthenticator.read(encryptedCode);
			} else {
				authenResult = this.bAuthenticator.read(mode, encryptedCode);
			}

			return this.convert(authenResult);

		} catch (Exception e) {
			e.printStackTrace();
			throw new CryptographyException(e, "Failed to decode encrypted code : {0}", encryptedCode);
		}
	}

	@Override
	public IDecodedResult decode(String mode, String encryptedCode, CodeType codeType) throws CryptographyException {
		try {

			IBSicpadataContent authenResult = null;

			if (mode == null || mode.trim().isEmpty()) {
				authenResult = this.bAuthenticator.read(encryptedCode);
			} else {
				authenResult = this.bAuthenticator.read(mode, encryptedCode);
			}

			return this.convert(authenResult);

		} catch (Exception e) {
			e.printStackTrace();
			throw new CryptographyException(e, "Failed to decode encrypted code : {0}", encryptedCode);
		}
	}
}
