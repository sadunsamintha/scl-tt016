package com.sicpa.tt079.remote.stdCrypto;

import com.sicpa.standard.sasscl.sicpadata.CryptographyException;
import com.sicpa.standard.sasscl.sicpadata.generator.EncoderEmptyException;
import com.sicpa.standard.sasscl.sicpadata.generator.impl.EncoderNoEncryptionSimulator;

import static com.sicpa.tt079.remote.impl.sicpadata.TT079SicpaDataGeneratorWrapper.BLOCK_SEPARATOR;

public class TT079EncoderNoEncryptionSimulator extends EncoderNoEncryptionSimulator {
	private static final long serialVersionUID = 1L;
	
    public TT079EncoderNoEncryptionSimulator(long batchid, int id, int min, int max, int year, long subsystemId, int codeTypeId) {
        super(batchid, id, min, max, year, subsystemId, codeTypeId);
    }

    public TT079EncoderNoEncryptionSimulator(long batchid, int id, int min, int max, int year, int codeTypeId) {
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
