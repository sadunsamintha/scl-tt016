package com.sicpa.tt065.sicpadata.generator.impl;

import com.sicpa.standard.sasscl.sicpadata.CryptographyException;
import com.sicpa.standard.sasscl.sicpadata.generator.EncoderEmptyException;
import com.sicpa.standard.sasscl.sicpadata.generator.impl.EncoderNoEncryptionSimulator;

import static com.sicpa.tt065.remote.impl.sicpadata.TT065SicpaDataGeneratorWrapper.BLOCK_SEPARATOR;

public class TT065EncoderNoEncryptionSimulator extends EncoderNoEncryptionSimulator {
    public TT065EncoderNoEncryptionSimulator(long batchid, int id, int min, int max, int year, long subsystemId, int codeTypeId) {
        super(batchid, id, min, max, year, subsystemId, codeTypeId);
    }

    public TT065EncoderNoEncryptionSimulator(long batchid, int id, int min, int max, int year, int codeTypeId) {
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
