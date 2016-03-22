package com.sicpa.tt018.scl.remoteServer.simu;

import java.util.Map;

import com.sicpa.standard.crypto.business.interfaces.IBAuthenticator;
import com.sicpa.standard.crypto.exceptions.CryptoException;
import com.sicpa.tt018.common.security.authenticator.AlbaniaAuthenticator;
import com.sicpa.tt018.interfaces.security.IAlbaniaAuthenResult;

public class AlbaniaSimuAuthenticator extends AlbaniaAuthenticator {

	private static final long serialVersionUID = 5795590472337940216L;

	private int internalSequence = 0;

	public AlbaniaSimuAuthenticator() {
		this(null, null);
	}

	public AlbaniaSimuAuthenticator(IBAuthenticator stdAuthenticator, Map<Integer, Long> mapBatchSizeByVersion) {
		super(stdAuthenticator, mapBatchSizeByVersion);
	}

	@Override
	public void load(String userPassword) throws CryptoException {

	}

	@Override
	public IAlbaniaAuthenResult authenticate(String code) throws CryptoException {

		int codeType = extractCodeType(code);

		AlbaniaSimuAuthenticResult authenticResult = new AlbaniaSimuAuthenticResult(codeType, internalSequence);
		internalSequence++;
		return authenticResult;
	}

	private int extractCodeType(String code) {
		String codeType = code.substring(2, 4);
		return Integer.parseInt(codeType);
	}

}
