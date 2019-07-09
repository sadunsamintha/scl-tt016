package com.sicpa.standard.sasscl.devices.remote.stdCrypto;

import com.sicpa.standard.sasscl.sicpadata.CryptographyException;
import com.sicpa.standard.sasscl.sicpadata.generator.EncoderEmptyException;
import com.sicpa.standard.sasscl.sicpadata.generator.impl.EncoderNoEncryptionSimulator;

import static com.sicpa.standard.sasscl.devices.remote.stdCrypto.StdHrdCryptoEncoderWrapperSimulator.BLOCK_SEPARATOR;

public class EncoderHrdNoEncryptionSimulator extends EncoderNoEncryptionSimulator {

	private static final long serialVersionUID = 1L;

	public EncoderHrdNoEncryptionSimulator(long batchid, int id, int min, int max, int year, long subsystemId, int codeTypeId) {
        super(batchid, id, min, max, year, subsystemId, codeTypeId);
    }

    public EncoderHrdNoEncryptionSimulator(long batchid, int id, int min, int max, int year, int codeTypeId) {
        super(batchid, id, min, max, year, codeTypeId);
    }

    @Override
    public String getEncryptedCode() throws CryptographyException {
        if (this.sequence > this.max) {
            throw new EncoderEmptyException();
        } else {
            updateDateOfUse();
            return String.valueOf(this.sequence) + BLOCK_SEPARATOR + String.format("%08d", this.sequence++);
        }
    }
}
