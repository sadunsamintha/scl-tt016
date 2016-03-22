package com.sicpa.tt018.scl.model.authenticator.utilities;

import com.sicpa.standard.sasscl.model.Code;
import com.sicpa.standard.sasscl.model.DecodedCameraCode;
import com.sicpa.standard.sasscl.sicpadata.CryptographyException;
import com.sicpa.standard.sasscl.sicpadata.reader.IAuthenticator;
import com.sicpa.tt018.scl.model.utilities.AlbaniaModelUtilities;

public class AlbaniaAuthenticatorUtilities {

	public static boolean isProductCodeDecoded(final Code code) {
		return !AlbaniaModelUtilities.isEmpty(code) && code.getEncoderId() != 0;
	}

	public static void populate(final Code code, IAuthenticator authenticator) throws CryptographyException {
		final DecodedCameraCode decodedCameraCode = getDecodedCameraCode(code, authenticator);
		code.setCodeType(decodedCameraCode.getCodeType());
		code.setEncoderId(decodedCameraCode.getBatchId());
		code.setSequence(decodedCameraCode.getSequence());
	}

	public static DecodedCameraCode getDecodedCameraCode(final Code code, IAuthenticator authenticator) throws CryptographyException {
		return (DecodedCameraCode) authenticator.decode("", code.getStringCode());
	}
}
