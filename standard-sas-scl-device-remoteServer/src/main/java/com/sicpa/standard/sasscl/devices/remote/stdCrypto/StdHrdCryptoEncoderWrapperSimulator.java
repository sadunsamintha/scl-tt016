package com.sicpa.standard.sasscl.devices.remote.stdCrypto;

import com.sicpa.standard.sasscl.sicpadata.CryptographyException;
import com.sicpa.standard.sicpadata.api.business.IBSicpadataGenerator;

import java.util.ArrayList;
import java.util.List;

public class StdHrdCryptoEncoderWrapperSimulator extends StdCryptoEncoderWrapperSimulator {

	public final static String BLOCK_SEPARATOR = ">-<" ;
	
	private static final long serialVersionUID = 1L;
	
    private long sequence = 1;

	public StdHrdCryptoEncoderWrapperSimulator(long batchid, int id, IBSicpadataGenerator encoder, int year, long subsystemId, long maxCode, ICryptoFieldsConfig cryptoFieldsConfig, int codeTypeId) {
        super(batchid, id, encoder, year, subsystemId, maxCode, cryptoFieldsConfig, codeTypeId);
    }
	
	@Override
    public synchronized List<String> getEncryptedCodes(long numberCodes) throws CryptographyException {
        List<String> res = super.getEncryptedCodes(numberCodes);

        List<String> codes = new ArrayList<String>();

        for(String s : res) {
        	codes.add(s + BLOCK_SEPARATOR + String.format("%08d", this.sequence++));
        }

        remainingCode -= res.size();
        return codes;
    }
}
