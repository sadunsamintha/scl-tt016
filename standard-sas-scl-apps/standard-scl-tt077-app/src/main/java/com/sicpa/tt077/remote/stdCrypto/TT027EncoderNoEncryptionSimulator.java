package com.sicpa.tt077.remote.stdCrypto;

import com.sicpa.standard.sasscl.sicpadata.CryptographyException;
import com.sicpa.standard.sasscl.sicpadata.generator.EncoderEmptyException;
import com.sicpa.standard.sasscl.sicpadata.generator.impl.EncoderNoEncryptionSimulator;

import static com.sicpa.tt077.remote.impl.sicpadata.TT077SicpaDataGeneratorWrapper.BLOCK_SEPARATOR;

public class TT027EncoderNoEncryptionSimulator extends EncoderNoEncryptionSimulator {
    public TT027EncoderNoEncryptionSimulator(long batchid, int id, int min, int max, int year, long subsystemId, int codeTypeId) {
        super(batchid, id, min, max, year, subsystemId, codeTypeId);
    }

    public TT027EncoderNoEncryptionSimulator(long batchid, int id, int min, int max, int year, int codeTypeId) {
        super(batchid, id, min, max, year, codeTypeId);
    }

    @Override
    public String getEncryptedCode() throws CryptographyException {
        if (this.sequence > this.max) {
            throw new EncoderEmptyException();
        } else {
            updateDateOfUse();
            return String.valueOf(this.sequence) + BLOCK_SEPARATOR + String.format("%09d", this.sequence++);
        }
    }
}
