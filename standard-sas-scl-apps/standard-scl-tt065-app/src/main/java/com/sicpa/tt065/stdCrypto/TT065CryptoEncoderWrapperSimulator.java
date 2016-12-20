package com.sicpa.tt065.stdCrypto;

import com.sicpa.standard.sasscl.devices.remote.stdCrypto.ICryptoFieldsConfig;
import com.sicpa.standard.sasscl.devices.remote.stdCrypto.StdCryptoEncoderWrapperSimulator;
import com.sicpa.standard.sasscl.sicpadata.CryptographyException;
import com.sicpa.standard.sicpadata.api.business.IBSicpadataGenerator;

import java.util.ArrayList;
import java.util.List;

import static com.sicpa.tt065.remote.impl.sicpadata.TT065SicpaDataGeneratorWrapper.BLOCK_SEPARATOR;


public class TT065CryptoEncoderWrapperSimulator extends StdCryptoEncoderWrapperSimulator {
    private static final long serialVersionUID = 1L;

    private long sequence = 0;

    public TT065CryptoEncoderWrapperSimulator(long batchid, int id, IBSicpadataGenerator encoder, int year, long subsystemId, long maxCode, ICryptoFieldsConfig cryptoFieldsConfig, int codeTypeId) {
        super(batchid, id, encoder, year, subsystemId, maxCode, cryptoFieldsConfig, codeTypeId);
    }

    @Override
    public synchronized List<String> getEncryptedCodes(long numberCodes) throws CryptographyException {
        List<String> res = super.getEncryptedCodes(numberCodes);

        List<String> codes = new ArrayList<String>();

        for(String s : res) {
            codes.add(s + BLOCK_SEPARATOR + String.format("%09d", sequence++));
        }

        remainingCode -= res.size();
        return codes;
    }
}
