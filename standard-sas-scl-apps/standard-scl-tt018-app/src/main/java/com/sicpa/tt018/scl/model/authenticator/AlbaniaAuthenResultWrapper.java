package com.sicpa.tt018.scl.model.authenticator;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.exception.InitializationRuntimeException;
import com.sicpa.standard.crypto.exceptions.CryptoException;
import com.sicpa.standard.sicpadata.api.business.IBSicpadataContent;
import com.sicpa.tt018.interfaces.security.IAlbaniaAuthenResult;
import com.sicpa.tt018.scl.model.authenticator.constants.AlbaniaAuthenticatorConstants;

public class AlbaniaAuthenResultWrapper implements IBSicpadataContent {

	private static final long serialVersionUID = -5389516357218315829L;

	private static Logger logger = LoggerFactory.getLogger(AlbaniaAuthenResultWrapper.class);

	private IAlbaniaAuthenResult albanianAuthenResult;
	private boolean isSerialNumberValid;
	private long serialNumber;

	public AlbaniaAuthenResultWrapper(final IAlbaniaAuthenResult authenResult) {
		super();
		setAlbanianAuthenResult(authenResult);

		initializeSerialNumberValid();
	}

	@Override
	public Map<String, Long> getFields() {
		final Map<String, Long> fields = new HashMap<String, Long>();
		fields.put(AlbaniaAuthenticatorConstants.BATCH_ID, getAlbanianAuthenResult().getBatchId());
		fields.put(AlbaniaAuthenticatorConstants.CODE_TYPE, getAlbanianAuthenResult().getType());
		fields.put(AlbaniaAuthenticatorConstants.SEQUENCE, getAlbanianAuthenResult().getSequence());
		if (isSerialNumberValid()) {
			fields.put(AlbaniaAuthenticatorConstants.SERIAL_NUMBER, getSerialNumber());
		}
		return fields;
	}

	@Override
	public String getMode() {
		// not used in albania result
		return "";
	}

	@Override
	public Integer getVersion() {
		return getAlbanianAuthenResult().getVersion();
	}

	public boolean isKnownVersion() {
		return getAlbanianAuthenResult().isKnownVersion();
	}

	@Override
	public boolean isValid() {
		return isSerialNumberValid() && getAlbanianAuthenResult().isValid();
	}

	protected void initializeSerialNumberValid() {
		try {
			setSerialNumber(getAlbanianAuthenResult().getSerialNumber());
			setSerialNumberValid(getSerialNumber() > 0l);

		} catch (CryptoException e) {
			logger.error("Error getting serial number ", e);
			setSerialNumberValid(false);
		}

	}

	protected void setAlbanianAuthenResult(final IAlbaniaAuthenResult authenResult) {
		if (authenResult == null) {
			throw new InitializationRuntimeException("Authenticator result is null");
		}
		albanianAuthenResult = authenResult;
	}

	protected IAlbaniaAuthenResult getAlbanianAuthenResult() {
		return albanianAuthenResult;
	}

	protected void setSerialNumber(long number) {
		serialNumber = number;
	}

	protected long getSerialNumber() {
		return serialNumber;
	}

	protected void setSerialNumberValid(boolean isValid) {
		isSerialNumberValid = isValid;
	}

	protected boolean isSerialNumberValid() {
		return isSerialNumberValid;
	}

}
