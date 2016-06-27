package com.sicpa.tt018.scl.model.encoder;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.crypto.exceptions.CryptoException;
import com.sicpa.standard.sasscl.devices.remote.stdCrypto.ICryptoFieldsConfig;
import com.sicpa.standard.sasscl.sicpadata.CryptographyException;
import com.sicpa.standard.sasscl.sicpadata.generator.AbstractEncoder;
import com.sicpa.standard.sasscl.sicpadata.generator.EncoderEmptyException;
import com.sicpa.standard.sasscl.sicpadata.generator.IEncoder;
import com.sicpa.tt018.interfaces.security.IAlbaniaEncoder;
import com.sicpa.tt018.interfaces.security.dto.SequenceDTO;
import com.sicpa.tt018.scl.model.encoder.constants.AlbaniaCryptoFieldsConfigConstants;
import com.sicpa.tt018.scl.model.encoder.constants.AlbaniaEncoderMessages;

public class AlbaniaEncoderWrapper extends AbstractEncoder implements IEncoder {
	private static final long serialVersionUID = -113596267885331264L;

	private static Logger logger = LoggerFactory.getLogger(AlbaniaEncoderWrapper.class);

	private IAlbaniaEncoder encoder;
	// password of the user - to reintroduce the user password after the
	// deserialization
	private String userPassword;

	private ICryptoFieldsConfig cryptoFieldsConfig;
	// used to know if a call to load is needed
	private transient Boolean firstCallToGetEncrypted = Boolean.TRUE;
	// current business encoder index
	private long currentIndex = 0;
	// SubsystemID for the encoder
	private int subsystemID;

	public AlbaniaEncoderWrapper(final long batchId, final long id, final String password,
			final ICryptoFieldsConfig cryptoFieldsConfig, final int year, final int subsystemID, final int codeTypeId,
			final IAlbaniaEncoder encoder) {
		super(batchId, id, year, subsystemID, codeTypeId);
		setAlbaniaEncoder(encoder);
		setUserPassword(password);
		setCryptoFieldsConfig(cryptoFieldsConfig);
		setSubsystemID(subsystemID);
		setOnClientDate(new Date());
	}

	@Override
	public String getEncryptedCode() throws CryptographyException {
		// load is needed after deserialized
		loadPassword();
		try {
			if (currentIndex >= getAlbaniaEncoder().getCapacity()) {
				throw new EncoderEmptyException();
			}
			// Generate next encoded code
			return getAlbaniaEncoder().getCode(getNextSequenceValue());
		} catch (final CryptoException e) {
			logger.error("Error while loading password = {1} , Exception = {0}.", e);
			throw new CryptographyException(e, AlbaniaEncoderMessages.EXCEPTION_ENCODERS_GETTING_ENCRYPTED_CODE);
		}
	}

	private synchronized void loadPassword() throws CryptographyException {
		try {
			if (isFirstCallToGetEncrypted()) {
				getAlbaniaEncoder().load(getUserPassword());
				setFirstCallToGetEncrypted(false);
			}
		} catch (final CryptoException e) {
			logger.error("Error while loading password = {1} , Exception = {0}.", e);
			throw new CryptographyException(AlbaniaEncoderMessages.EXCEPTION_ENCODERS_LOADING_PASSWORD, e);
		}
	}

	private SequenceDTO getNextSequenceValue() {
		final SequenceDTO seq = new SequenceDTO();
		// get next sequenceDTO value from CryptoFieldsConfig
		seq.setSequence(getFields(this).get(AlbaniaCryptoFieldsConfigConstants.SEQUENCE));
		setCurrentIndex(getCurrentIndex() + 1);
		return seq;
	}

	public Map<String, Long> getFields(AlbaniaEncoderWrapper encoder) {
		Map<String, Long> res = new HashMap<>();
		res.put(AlbaniaCryptoFieldsConfigConstants.SEQUENCE, ((AlbaniaEncoderWrapper) encoder).getCurrentIndex());
		return res;
	}

	@Override
	public synchronized boolean isEncoderEmpty() {
		return getRemainingCodes() <= 0;
	}

	public synchronized long getRemainingCodes() {
		return getAlbaniaEncoder().getCapacity() - getCurrentIndex();
	}

	protected void setCurrentIndex(final long index) {
		currentIndex = index;
	}

	public long getCurrentIndex() {
		return currentIndex;
	}

	protected void setAlbaniaEncoder(final IAlbaniaEncoder encoder) {
		this.encoder = encoder;
	}

	protected IAlbaniaEncoder getAlbaniaEncoder() {
		return encoder;
	}

	protected void setUserPassword(final String password) {
		userPassword = password;
		setFirstCallToGetEncrypted(true);
	}

	protected String getUserPassword() {
		return userPassword;
	}

	protected void setCryptoFieldsConfig(final ICryptoFieldsConfig cryptoFieldsConfig) {
		this.cryptoFieldsConfig = cryptoFieldsConfig;
	}

	protected ICryptoFieldsConfig getCryptoFieldsConfig() {
		return cryptoFieldsConfig;
	}

	protected void setFirstCallToGetEncrypted(final Boolean firstCall) {
		firstCallToGetEncrypted = firstCall;
	}

	protected Boolean isFirstCallToGetEncrypted() {
		return null == firstCallToGetEncrypted ? Boolean.TRUE : firstCallToGetEncrypted;
	}

	public void setSubsystemID(final int subsystemID) {
		this.subsystemID = subsystemID;
	}

	public int getSubsystemID() {
		return subsystemID;
	}

}
