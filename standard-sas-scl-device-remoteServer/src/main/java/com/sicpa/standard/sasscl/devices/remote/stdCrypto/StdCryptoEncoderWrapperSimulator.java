package com.sicpa.standard.sasscl.devices.remote.stdCrypto;

import com.sicpa.standard.sasscl.sicpadata.CryptographyException;
import com.sicpa.standard.sicpadata.api.business.IBSicpadataGenerator;

import java.util.List;

public class StdCryptoEncoderWrapperSimulator extends StdCryptoEncoderWrapper {

	private static final long serialVersionUID = 1L;

	protected long remainingCode;

	public StdCryptoEncoderWrapperSimulator(final long batchid, int id, IBSicpadataGenerator encoder, int year,
											long subsystemId, long maxCode, ICryptoFieldsConfig cryptoFieldsConfig, int codeTypeId) {
		super(batchid,id, encoder, year, subsystemId, cryptoFieldsConfig, codeTypeId);
		this.remainingCode = maxCode;
	}

	@Override
	public synchronized long getRemainingCodes() {
		return remainingCode;
	}

	@Override
	public synchronized List<String> getEncryptedCodes(long numberCodes) throws CryptographyException {
		List<String> res = super.getEncryptedCodes(numberCodes);
		remainingCode -= res.size();
		return res;
	}

	public void setRemainingCode(long remainingCode) {
		this.remainingCode = remainingCode;
	}
}
