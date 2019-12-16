package com.sicpa.tt018.scl.model.authenticator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.exception.InitializationRuntimeException;
import com.sicpa.standard.crypto.exceptions.CryptoException;
import com.sicpa.standard.sasscl.devices.remote.stdCrypto.ICryptoFieldsConfig;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.sicpadata.CryptographyException;
import com.sicpa.standard.sasscl.sicpadata.reader.IAuthenticator;
import com.sicpa.standard.sasscl.sicpadata.reader.IDecodedResult;
import com.sicpa.tt018.interfaces.security.IAlbaniaAuthenResult;
import com.sicpa.tt018.interfaces.security.IAlbaniaAuthenticator;

public class AlbaniaAuthenticatorWrapper implements IAuthenticator {
	private static final long serialVersionUID = -2838469820793624969L;

	private static Logger logger = LoggerFactory.getLogger(AlbaniaAuthenticatorWrapper.class);

	private IAlbaniaAuthenticator albanianAuthenticator;
	private String userPassword;
	private ICryptoFieldsConfig cryptoFieldsConfig;
	private boolean firstCallToDecode = true;

	public AlbaniaAuthenticatorWrapper(final IAlbaniaAuthenticator albanianAuthenticator, final String userPassword, final ICryptoFieldsConfig cryptoFieldsConfig) {
		super();
		setAlbanianAuthenticator(albanianAuthenticator);
		setCryptoFieldsConfig(cryptoFieldsConfig);
		setUserPassword(userPassword);
	}

	@Override
	public IDecodedResult decode(String mode, String encryptedCode) throws CryptographyException {

		if (encryptedCode == null) {
			logger.warn("Code to authenticate is null");
			return null;
		}

		try {

			IAlbaniaAuthenResult authenResult = null;

			// load is needed after deserialized
			// only executed the first time
			loadPassword();

			authenResult = this.albanianAuthenticator.authenticate(encryptedCode);

			return this.convert(authenResult);

		} catch (Exception e) {
			logger.error("Error while authenticating ", e);
			throw new CryptographyException(e, "Failed to decode encrypted code : {0}", encryptedCode);
		}

	}
	
	/**
	 * Interface IAuthenticator.java has a new method signature with ProductionParameters 
	 * as a new parameter which is not called here in Albania (TT018)
	 * Its implementation is only used in Morocco (TT016)
	 */
	@Override
	public IDecodedResult decode(String mode, String encryptedCode, CodeType codeType) throws CryptographyException {
		return null;
	}

	protected synchronized void loadPassword() throws CryptoException {
		if (isFirstCallToDecode()) {
			getAlbanianAuthenticator().load(getUserPassword());
			setFirstCallToDecode(false);
		}
	}

	protected IDecodedResult convert(final IAlbaniaAuthenResult authenResult) {
		// NULL arg check
		if (authenResult == null) {
			logger.warn("Authenticate Result is null");
			return null;
		}

		return getCryptoFieldsConfig().getFields(new AlbaniaAuthenResultWrapper(authenResult));
	}

	protected void setFirstCallToDecode(final Boolean firstCall) {
		firstCallToDecode = firstCall;
	}

	protected boolean isFirstCallToDecode() {
		return firstCallToDecode;
	}

	protected void setAlbanianAuthenticator(final IAlbaniaAuthenticator authenticator) {
		// NULL arg check
		if (authenticator == null) {
			throw new InitializationRuntimeException("Authenticator is null");
		}
		albanianAuthenticator = authenticator;
	}

	protected IAlbaniaAuthenticator getAlbanianAuthenticator() {
		return albanianAuthenticator;
	}

	protected void setUserPassword(final String password) {
		userPassword = password;
		setFirstCallToDecode(true);
	}

	protected String getUserPassword() {
		return userPassword;
	}

	protected void setCryptoFieldsConfig(final ICryptoFieldsConfig config) {
		// NULL arg checks
		if (config == null) {
			throw new InitializationRuntimeException("Authenticator crypto config is null");
		}
		cryptoFieldsConfig = config;
	}

	protected ICryptoFieldsConfig getCryptoFieldsConfig() {
		return cryptoFieldsConfig;
	}
}
