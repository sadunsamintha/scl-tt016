package com.sicpa.standard.sasscl.devices.remote.stdCrypto;

import com.sicpa.standard.sicpadata.api.business.IBSicpadataReader;

public class StdCryptoAuthenticatorWrapperSimulator extends StdCryptoAuthenticatorWrapper {

	private static final long serialVersionUID = 1L;

	public StdCryptoAuthenticatorWrapperSimulator(IBSicpadataReader authenticator,
			ICryptoFieldsConfig cryptoFieldsConfig) {
		super(authenticator, cryptoFieldsConfig);
	}

}
